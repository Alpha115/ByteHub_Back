package com.bytehub.notification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.bytehub.member.MemberDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotiService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final MemberDAO memberDAO;
    
    // 메모리 기반 알림 저장소
    private final Map<String, List<NotiDTO>> userNotifications = new ConcurrentHashMap<>();
    
    /**
     * 특정 사용자에게 알림 전송
     */
    public void sendNotification(String user_id, String type, String title, String content) {
        sendNotification(user_id, type, title, content, null);
    }
    
    /**
     * 특정 사용자에게 알림 전송 (target_url 포함)
     */
    public void sendNotification(String user_id, String type, String title, String content, String target_url) {
        if ("all".equals(user_id)) {
            // 모든 사용자에게 알림 전송 (작성자 제외)
            List<String> allUsers = getAllUserIds();
            String writerId = extractWriterIdFromContent(content); // 내용에서 작성자 ID 추출
            
            for (String userId : allUsers) {
                if (!userId.equals(writerId)) { // 작성자 제외
                    NotiDTO notification = new NotiDTO(userId, type, title, content);
                    notification.setNotification_id(UUID.randomUUID().toString());
                    notification.setTarget_url(target_url);
                    
                    // 메모리에 저장
                    userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);
                    
                    // WebSocket으로 실시간 전송
                    sendToUser(userId, notification);
                }
            }
            log.info("전체 사용자 알림 전송 (작성자 제외): {} (타입: {})", title, type);
        } else {
            // 개별 사용자에게 알림 전송
            NotiDTO notification = new NotiDTO(user_id, type, title, content);
            notification.setNotification_id(UUID.randomUUID().toString());
            notification.setTarget_url(target_url);
            
            // 메모리에 저장
            userNotifications.computeIfAbsent(user_id, k -> new ArrayList<>()).add(notification);
            
            // WebSocket으로 실시간 전송
            sendToUser(user_id, notification);
            
            log.info("알림 전송: {} -> {} (타입: {})", title, user_id, type);
        }
    }
    
    /**
     * WebSocket을 통해 특정 사용자에게 알림 전송
     */
    private void sendToUser(String user_id, NotiDTO notification) {
        try {
            messagingTemplate.convertAndSend("/topic/notification/" + user_id, notification);
        } catch (Exception e) {
            log.info("알림 전송 실패: {} -> {}", user_id, e.getMessage());
        }
    }
    
    /**
     * 사용자의 미확인 알림 조회
     */
    public List<NotiDTO> getUnreadNotifications(String user_id) {
        return userNotifications.getOrDefault(user_id, new ArrayList<>())
            .stream()
            .filter(n -> !n.isRead())
            .collect(Collectors.toList());
    }
    
    /**
     * 사용자의 모든 알림 조회
     */
    public List<NotiDTO> getAllNotifications(String user_id) {
        return userNotifications.getOrDefault(user_id, new ArrayList<>());
    }
    
    /**
     * 특정 알림 조회
     */
    public NotiDTO getNotificationById(String notificationId, String user_id) {
        List<NotiDTO> notifications = userNotifications.get(user_id);
        if (notifications != null) {
            return notifications.stream()
                .filter(n -> n.getNotification_id().equals(notificationId))
                .findFirst()
                .orElse(null);
        }
        return null;
    }
    
    /**
     * 알림을 읽음 처리
     */
    public void markAsRead(String user_id, String notification_id) {
        List<NotiDTO> notifications = userNotifications.get(user_id);
        if (notifications != null) {
            notifications.stream()
                .filter(n -> n.getNotification_id().equals(notification_id))
                .findFirst()
                .ifPresent(n -> n.setRead(true));
        }
    }
    
    /**
     * 알림 삭제
     */
    public void deleteNotification(String user_id, String notification_id) {
        List<NotiDTO> notifications = userNotifications.get(user_id);
        if (notifications != null) {
            notifications.removeIf(n -> n.getNotification_id().equals(notification_id));
        }
    }
    
    /**
     * 사용자의 모든 알림 삭제
     */
    public void deleteAllNotifications(String user_id) {
        userNotifications.remove(user_id);
    }
    
    /**
     * 24시간이 지난 알림 자동 삭제 (1시간마다 실행)
     */
    @Scheduled(fixedRate = 3600000) // 1시간 = 3600000ms
    public void cleanupExpiredNotifications() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        final int[] totalDeleted = {0};
        
        userNotifications.forEach((userId, notifications) -> {
            int beforeSize = notifications.size();
            notifications.removeIf(n -> n.getCreatedAt().isBefore(cutoffTime));
            int afterSize = notifications.size();
            totalDeleted[0] += (beforeSize - afterSize);
        });
        
        if (totalDeleted[0] > 0) {
            log.info("만료된 알림 {}개 정리 완료", totalDeleted[0]);
        }
    }
    
    /**
     * 채팅 메시지 알림 전송 (채팅 전용)
     */
    public void sendChatNotification(String user_id, String sender_id, String chat_name, String message) {
        sendNotification(
            user_id,
            "CHAT_MESSAGE",
            "새로운 채팅 메시지",
            sender_id + "님이 " + chat_name + "에서 메시지를 보냈습니다: " + message,
            "/chat"
        );
    }
    
    /**
     * 채팅방 초대 알림 전송
     */
    public void sendChatInviteNotification(String userId, String chatName, String inviterId) {
        sendNotification(
            userId,
            "CHAT_INVITE",
            "채팅방 초대",
            inviterId + "님이 " + chatName + " 채팅방에 초대했습니다.",
            "/chat"
        );
    }
    
    /**
     * 결재 문서 생성 알림 전송 (결재자들에게)
     */
    public void sendApprovalRequestNotification(String checkerId, String writerName, String subject, String apprType) {
        sendNotification(
            checkerId,
            "APPROVAL_REQUEST",
            "결재 요청",
            writerName + "님이 " + apprType + " 결재를 요청했습니다: " + subject,
            "/approval"
        );
    }
    
    /**
     * 결재 상태 변경 알림 전송 (기안자에게)
     */
    public void sendApprovalStatusNotification(String writerId, String checkerName, String subject, String status) {
        String statusText = "";
        switch (status) {
            case "승인":
                statusText = "승인";
                break;
            case "반려":
                statusText = "반려";
                break;
            case "승인완료":
                statusText = "최종 승인 완료";
                break;
            default:
                statusText = status;
        }
        
        sendNotification(
            writerId,
            "APPROVAL_STATUS",
            "결재 상태 변경",
            checkerName + "님이 " + subject + " 결재를 " + statusText + "했습니다.",
            "/approval"
        );
    }
    
    /**
     * 파일 업로드 알림 전송 (같은 부서 멤버들에게)
     */
    public void sendFileUploadNotification(String userId, String fileName, String deptName, int deptIdx) {
        // 같은 부서의 모든 사용자에게 알림 전송
        List<String> deptMembers = getDeptMemberIds(deptIdx);
        for (String memberId : deptMembers) {
            if (!memberId.equals(userId)) { // 업로드한 사용자 제외
                sendNotification(
                    memberId,
                    "FILE_UPLOAD",
                    "새 파일 업로드",
                    userId + "님이 " + deptName + " 팀 파일함에 " + fileName + " 파일을 업로드했습니다.",
                    "/cloud"
                );
            }
        }
    }
    
    /**
     * 링크 저장 알림 전송 (같은 부서 멤버들에게)
     */
    public void sendLinkSaveNotification(String userId, String linkName, String deptName, int deptIdx) {
        // 같은 부서의 모든 사용자에게 알림 전송
        List<String> deptMembers = getDeptMemberIds(deptIdx);
        for (String memberId : deptMembers) {
            if (!memberId.equals(userId)) { // 저장한 사용자 제외
                sendNotification(
                    memberId,
                    "LINK_SAVE",
                    "새 링크 저장",
                    userId + "님이 " + deptName + " 팀 파일함에 " + linkName + " 링크를 저장했습니다.",
                    "/cloud"
                );
            }
        }
    }
    
    /**
     * 특정 부서의 모든 사용자 ID 조회
     */
    private List<String> getDeptMemberIds(int deptIdx) {
        try {
            ArrayList<Map<String, Object>> allUsers = memberDAO.users();
            return allUsers.stream()
                .filter(user -> {
                    Object deptIdxObj = user.get("dept_idx");
                    return deptIdxObj != null && deptIdxObj.toString().equals(String.valueOf(deptIdx));
                })
                .map(user -> (String) user.get("user_id"))
                .filter(userId -> userId != null && !userId.isEmpty())
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("부서 사용자 목록 조회 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 멤버 정보 변경 알림 전송 (본인에게)
     */
    public void sendMemberInfoChangeNotification(String userId, String changeType, String oldValue, String newValue) {
        String changeText = "";
        switch (changeType) {
            case "DEPT":
                changeText = "부서가 " + oldValue + "에서 " + newValue + "로 변경되었습니다.";
                break;
            case "LEVEL":
                changeText = "직급이 " + oldValue + "에서 " + newValue + "로 변경되었습니다.";
                break;
            case "AUTHORITY":
                changeText = "권한이 " + oldValue + "에서 " + newValue + "로 변경되었습니다.";
                break;
            default:
                changeText = changeType + "이(가) " + oldValue + "에서 " + newValue + "로 변경되었습니다.";
        }
        
        sendNotification(
            userId,
            "MEMBER_INFO_CHANGE",
            "멤버 정보 변경",
            changeText,
            "/mypage"
        );
    }
    
    /**
     * 모든 사용자 ID 목록 조회
     */
    private List<String> getAllUserIds() {
        try {
            ArrayList<Map<String, Object>> users = memberDAO.users();
            return users.stream()
                .map(user -> (String) user.get("user_id"))
                .filter(userId -> userId != null && !userId.isEmpty())
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("사용자 목록 조회 실패: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 알림 내용에서 작성자 ID 추출
     * 예: "user123님이 '공지사항 제목' 공지사항을 등록했습니다." → "user123"
     */
    private String extractWriterIdFromContent(String content) {
        try {
            if (content != null && content.contains("님이")) {
                return content.substring(0, content.indexOf("님이"));
            }
        } catch (Exception e) {
            log.warn("작성자 ID 추출 실패: {}", e.getMessage());
        }
        return null;
    }
} 