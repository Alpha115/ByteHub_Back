package com.bytehub.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytehub.approval.ApprDTO;
import com.bytehub.attendance.AttDTO;
import com.bytehub.board.BoardDTO;
import com.bytehub.chat.ChatMessageDTO;
import com.bytehub.cloud.CloudDTO;
import com.bytehub.member.MemberDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotiController {
    
    private final NotiService notiService;
    
    /**
     * 사용자의 미확인 알림 조회
     */
    @GetMapping("notification/unread")
    public Map<String, Object> getUnreadNotifications(@RequestParam String user_id) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<NotiDTO> notifications = notiService.getUnreadNotifications(user_id);
            result.put("success", true);
            result.put("data", notifications);
            result.put("count", notifications.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "알림 조회 실패: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 사용자의 모든 알림 조회
     */
    @GetMapping("notification/all")
    public Map<String, Object> getAllNotifications(@RequestParam String user_id) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<NotiDTO> notifications = notiService.getAllNotifications(user_id);
            result.put("success", true);
            result.put("data", notifications);
            result.put("count", notifications.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "알림 조회 실패: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 알림을 읽음 처리
     */
    @PostMapping("notification/read/{notificationId}")
    public Map<String, Object> markAsRead(@PathVariable String notificationId, @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String user_id = request.get("user_id");
            notiService.markAsRead(user_id, notificationId);
            result.put("success", true);
            result.put("message", "알림이 읽음 처리되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "알림 읽음 처리 실패: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 특정 알림 삭제
     */
    @DeleteMapping("notification/{notificationId}")
    public Map<String, Object> deleteNotification(@PathVariable String notificationId, @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String user_id = request.get("user_id");
            notiService.deleteNotification(user_id, notificationId);
            result.put("success", true);
            result.put("message", "알림이 삭제되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "알림 삭제 실패: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 사용자의 모든 알림 삭제
     */
    @DeleteMapping("notification/all")
    public Map<String, Object> deleteAllNotifications(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String user_id = request.get("user_id");
            notiService.deleteAllNotifications(user_id);
            result.put("success", true);
            result.put("message", "모든 알림이 삭제되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "알림 삭제 실패: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("notification/send")
    public Map<String, Object> sendNoti(@RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String user_id = request.get("user_id");
            String type = request.get("type");
            String title = request.get("title");
            String content = request.get("content");
            
            notiService.sendNotification(user_id, type, title, content);
            
            result.put("success", true);
            result.put("message", "알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "알림 전송 실패: " + e.getMessage());
        }
        return result;
}
    // 1. 채팅방 초대 시
    @PostMapping("notification/chat/invite")
    public Map<String, Object> inviteToChat(@RequestBody Map<String, Object> invite) {
        Map<String, Object> result = new HashMap<>();
        try {
            String targetUserId = (String) invite.get("target_user_id");
            String chatName = (String) invite.get("chat_name");
            String inviterId = (String) invite.get("inviter_id");
            
            // 채팅방 초대 로직...
            
            // 알림 전송 (채팅방 이름 포함)
            notiService.sendChatInviteNotification(targetUserId, chatName, inviterId);
            
            result.put("success", true);
            result.put("message", "채팅방 초대 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "채팅방 초대 실패: " + e.getMessage());
        }
        return result;
    }

    // 2. 채팅 메시지 전송 시
    @PostMapping("notification/chat/message")
    public Map<String, Object> sendChatMessage(@RequestBody ChatMessageDTO message) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 채팅 메시지 저장...
            
            // 채팅방의 다른 멤버들에게 알림 (채팅방 이름 포함)
            String chatName = "채팅방"; // 기본값, 필요시 chat_idx로 채팅방 이름 조회 가능
            notiService.sendChatNotification(message.getUser_id(), message.getUser_id(), chatName, message.getContent());
            
            result.put("success", true);
            result.put("message", "채팅 메시지 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "채팅 메시지 전송 실패: " + e.getMessage());
        }
        return result;
    }


    // 4. 프로젝트 초대 시
    @PostMapping("notification/project/invite")
    public Map<String, Object> inviteToProject(@RequestBody Map<String, Object> invite) {
        Map<String, Object> result = new HashMap<>();
        try {
            String targetUserId = (String) invite.get("target_user_id");
            String projectName = (String) invite.get("project_name");
            String inviterId = (String) invite.get("inviter_id");
            
            // 프로젝트 초대 로직...
            
            // 초대받은 사용자에게 알림
            notiService.sendProjectInviteNotification(targetUserId, projectName, inviterId);
            
            result.put("success", true);
            result.put("message", "프로젝트 초대 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "프로젝트 초대 실패: " + e.getMessage());
        }
        return result;
    }

    // 5. 결재 생성 시
    @PostMapping("notification/approval/create")
    public Map<String, Object> createApproval(@RequestBody ApprDTO approval) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 결재 생성...
            
            // 결재자들에게 알림 (실제로는 결재 라인에서 결재자들을 조회해야 함)
            notiService.sendNotification("admin", "APPROVAL_REQUEST", 
                "결재 요청", 
                approval.getWriter_id() + "님이 " + approval.getSubject() + " 결재를 요청했습니다.");
            
            result.put("success", true);
            result.put("message", "결재 요청 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "결재 생성 실패: " + e.getMessage());
        }
        return result;
    }

    // 6. 결재 상태 변경 시
    @PostMapping("notification/approval/update-status")
    public Map<String, Object> updateApprovalStatus(@RequestBody Map<String, Object> status) {
        Map<String, Object> result = new HashMap<>();
        try {
            String authorId = (String) status.get("writer_id");
            String approvalStatus = (String) status.get("status");
            
            // 결재 상태 변경...
            
            // 결재 작성자에게 알림
            notiService.sendNotification(authorId, "APPROVAL_STATUS", 
                "결재 상태 변경", 
                "귀하의 결재가 " + approvalStatus + " 처리되었습니다.");
            
            result.put("success", true);
            result.put("message", "결재 상태 변경 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "결재 상태 변경 실패: " + e.getMessage());
        }
        return result;
    }

    // 8. 파일/링크 업로드 시
    @PostMapping("notification/files/upload")
    public Map<String, Object> uploadFile(@RequestBody CloudDTO file) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 파일 업로드...
            
            // 같은 부서 사용자들에게 알림
            notiService.sendNotification("dept_" + file.getDept_idx(), "FILE_UPLOAD", 
                "파일 업로드", 
                file.getUser_id() + "님이 " + file.getFilename() + " 파일을 업로드했습니다.",
                "/component/files"); // target_url 추가
                
            result.put("success", true);
            result.put("message", "파일 업로드 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "파일 업로드 실패: " + e.getMessage());
        }
        return result;
    }

    // 9. 공지사항 등록 시
    @PostMapping("notification/board/create")
    public Map<String, Object> createNotice(@RequestBody BoardDTO notice) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 공지사항 등록...
            
            // 모든 사용자에게 알림
            notiService.sendNotification("all", "BOARD_NOTICE", 
                "새 공지사항", 
                notice.getUser_id() + "님이 " + notice.getSubject() + " 공지사항을 등록했습니다.");
            
            result.put("success", true);
            result.put("message", "공지사항 등록 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "공지사항 등록 실패: " + e.getMessage());
        }
        return result;
    }

    // 10. 사용자 정보 수정 시
    @PostMapping("notification/member/update")
    public Map<String, Object> updateMember(@RequestBody MemberDTO member) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 사용자 정보 수정...
            
            // 해당 사용자에게 알림
            notiService.sendNotification(member.getUser_id(), "MEMBER_UPDATE", 
                "사용자 정보 변경", 
                "귀하의 사용자 정보가 변경되었습니다.");
            
            result.put("success", true);
            result.put("message", "사용자 정보 변경 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "사용자 정보 변경 실패: " + e.getMessage());
        }
        return result;
    }

    // 11. 회의록 작성 시 참석자들에게 알림 전송
    @PostMapping("notification/meeting/invite")
    public Map<String, Object> sendMeetingInvite(@RequestBody Map<String, Object> meeting) {
        Map<String, Object> result = new HashMap<>();
        try {
            String writerId = (String) meeting.get("writer_id");
            String subject = (String) meeting.get("subject");
            List<String> attendees = (List<String>) meeting.get("attendees");
            
            for (String attendeeId : attendees) {
                if (!attendeeId.equals(writerId)) {
                    notiService.sendNotification(attendeeId, "MEETING_INVITE", 
                        "회의록 초대", 
                        writerId + "님이 회의록 '" + subject + "'에 초대했습니다.");
                }
            }
            
            result.put("success", true);
            result.put("message", "회의록 초대 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "회의록 초대 알림 전송 실패: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 알림 클릭 시 URL로 이동하는 기능
     */
    @PostMapping("click/{notificationId}")
    public Map<String, Object> handleNotificationClick(@PathVariable String notificationId, @RequestBody Map<String, String> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String user_id = request.get("user_id");
            
            // 알림 정보 조회
            NotiDTO notification = notiService.getNotificationById(notificationId, user_id);
            
            if (notification == null) {
                result.put("success", false);
                result.put("message", "알림을 찾을 수 없습니다.");
                return result;
            }
            
            // 알림을 읽음 처리
            notiService.markAsRead(user_id, notificationId);
            
            // 알림 타입에 따른 URL 생성
            String targetUrl = generateTargetUrl(notification);
            
            result.put("success", true);
            result.put("target_url", targetUrl);
            result.put("notification_type", notification.getType());
            result.put("message", "알림 클릭 처리 완료");
            
            log.info("알림 클릭 처리: notificationId={}, user_id={}, targetUrl={}", notificationId, user_id, targetUrl);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "알림 클릭 처리 실패: " + e.getMessage());
            log.info("알림 클릭 처리 실패: {}", e.getMessage(), e);
        }
        return result;
    }
    
    /**
     * 알림 타입에 따른 URL 생성
     */
    private String generateTargetUrl(NotiDTO notification) {
        // 알림에 저장된 target_url이 있으면 우선 사용
        if (notification.getTarget_url() != null && !notification.getTarget_url().isEmpty()) {
            return notification.getTarget_url();
        }
        
        // 알림 타입에 따른 기본 URL 생성
        switch (notification.getType()) {
            case "CHAT_MESSAGE":
            case "CHAT_INVITE":
                return "/component/chating"; // 채팅 페이지로 이동
                
            case "APPROVAL_REQUEST":
            case "APPROVAL_STATUS":
                return "/component/approval"; // 결재 페이지로 이동
                
            case "FILE_UPLOAD":
            case "LINK_SAVE":
                return "/component/files"; // 클라우드 페이지로 이동
                
            case "BOARD_NOTICE":
                return "/component/board"; // 게시판 페이지로 이동
                
            case "MEMBER_INFO_CHANGE":
                return "/component/mypage"; // 마이페이지로 이동
                
            case "PROJECT_INVITE":
                return "/component/project"; // 프로젝트 페이지로 이동
                
            case "MEETING_INVITE":
                return "/component/meeting"; // 회의록 페이지로 이동
                
            default:
                return "/component/main"; // 기본 홈페이지로 이동
        }
    }
} 