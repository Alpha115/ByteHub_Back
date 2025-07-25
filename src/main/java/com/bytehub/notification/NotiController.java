package com.bytehub.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin
@RequestMapping("/notification")
@Slf4j
public class NotiController {
    
    private final NotiService notiService;
    
    /**
     * 사용자의 미확인 알림 조회
     */
    @GetMapping("/unread")
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
    @GetMapping("/all")
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
    @PostMapping("/read/{notificationId}")
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
    @DeleteMapping("/{notificationId}")
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
    @DeleteMapping("/all")
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

    @PostMapping("/send")
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
    @PostMapping("/chat/invite")
    public Map<String, Object> inviteToChat(@RequestBody Map<String, Object> invite) {
        Map<String, Object> result = new HashMap<>();
        try {
            String targetUserId = (String) invite.get("target_user_id");
            String chatName = (String) invite.get("chat_name");
            String inviterId = (String) invite.get("inviter_id");
            
            // 채팅방 초대 로직...
            
            // 알림 전송
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
    @PostMapping("/chat/message")
    public Map<String, Object> sendChatMessage(@RequestBody ChatMessageDTO message) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 채팅 메시지 저장...
            
            // 채팅방의 다른 멤버들에게 알림
            notiService.sendChatNotification(message.getUser_id(), message.getUser_id(), "채팅방", message.getContent());
            
            result.put("success", true);
            result.put("message", "채팅 메시지 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "채팅 메시지 전송 실패: " + e.getMessage());
        }
        return result;
    }

    // 3. 출근/퇴근 완료 시
    @PostMapping("/attendance/complete")
    public Map<String, Object> completeAttendance(@RequestBody AttDTO attendance) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 출근/퇴근 처리...
            
            // 관리자에게 알림
            notiService.sendNotification("admin", "ATTENDANCE_COMPLETE", 
                attendance.getAtt_type() + " 완료", 
                attendance.getUser_id() + "님이 " + attendance.getAtt_type() + "을 완료했습니다.");
            
            result.put("success", true);
            result.put("message", "근태 처리 완료 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "근태 처리 실패: " + e.getMessage());
        }
        return result;
    }

    // 4. 프로젝트 초대 시
    @PostMapping("/project/invite")
    public Map<String, Object> inviteToProject(@RequestBody Map<String, Object> invite) {
        Map<String, Object> result = new HashMap<>();
        try {
            String targetUserId = (String) invite.get("target_user_id");
            String projectName = (String) invite.get("project_name");
            String inviterId = (String) invite.get("inviter_id");
            
            // 프로젝트 초대 로직...
            
            // 초대받은 사용자에게 알림
            notiService.sendNotification(targetUserId, "PROJECT_INVITE", 
                "프로젝트 초대", 
                inviterId + "님이 " + projectName + " 프로젝트에 초대했습니다.");
            
            result.put("success", true);
            result.put("message", "프로젝트 초대 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "프로젝트 초대 실패: " + e.getMessage());
        }
        return result;
    }

    // 5. 결재 생성 시
    @PostMapping("/approval/create")
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
    @PostMapping("/approval/update-status")
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

    // 7. 비상연락망 이메일 발송 시
    @PostMapping("/emergency/send-email")
    public Map<String, Object> sendEmergencyEmail(@RequestBody Map<String, Object> email) {
        Map<String, Object> result = new HashMap<>();
        try {
            String recipientId = (String) email.get("recipient_id");
            String subject = (String) email.get("subject");
            
            // 이메일 발송...
            
            // 수신자에게 알림
            notiService.sendNotification(recipientId, "EMERGENCY_EMAIL", 
                "비상 연락망 이메일", 
                "비상 연락망 관련 이메일이 발송되었습니다: " + subject);
            
            result.put("success", true);
            result.put("message", "비상 연락망 이메일 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "비상 연락망 이메일 발송 실패: " + e.getMessage());
        }
        return result;
    }

    // 8. 파일/링크 업로드 시
    @PostMapping("/cloud/upload")
    public Map<String, Object> uploadFile(@RequestBody CloudDTO file) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 파일 업로드...
            
            // 같은 부서 사용자들에게 알림
            notiService.sendNotification("dept_" + file.getDept_idx(), "FILE_UPLOAD", 
                "파일 업로드", 
                file.getUser_id() + "님이 " + file.getFilename() + " 파일을 업로드했습니다.");
            
            result.put("success", true);
            result.put("message", "파일 업로드 알림이 전송되었습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "파일 업로드 실패: " + e.getMessage());
        }
        return result;
    }

    // 9. 공지사항 등록 시
    @PostMapping("/board/create")
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
    @PostMapping("/member/update")
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
    @PostMapping("/board/meeting/invite")
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
} 