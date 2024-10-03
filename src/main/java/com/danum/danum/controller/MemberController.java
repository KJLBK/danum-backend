package com.danum.danum.controller;

import com.danum.danum.domain.member.*;
import com.danum.danum.service.admin.AdminService;
import com.danum.danum.service.member.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final AdminService adminService; // 241003추가

    @PostMapping("/join")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        Member member = memberService.join(registerDto);
        return ResponseEntity.ok(member);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody UpdateDto updateDto){
        Member member = memberService.update(updateDto);
        return ResponseEntity.ok(member);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        String accessToken = memberService.login(loginDto, response);
        return ResponseEntity.ok(accessToken);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<?> delete(@PathVariable("email") String email) {
        memberService.delete(email);
        return ResponseEntity.ok("회원탈퇴에 성공하였습니다.");
    }


    @GetMapping("/profile-image")
    public ResponseEntity<String> getProfileImage(Authentication authentication) {
        String email = authentication.getName();
        String profileImageUrl = memberService.getProfileImageUrl(email);
        return ResponseEntity.ok(profileImageUrl);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{email}/activate")
    public ResponseEntity<?> activateMember(@PathVariable("email") String email) {
        memberService.activateMember(email);
        return ResponseEntity.ok("회원이 활성화되었습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{email}/deactivate")
    public ResponseEntity<?> deactivateMember(@PathVariable("email") String email) {
        memberService.deactivateMember(email);
        return ResponseEntity.ok("회원이 비활성화되었습니다.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all-emails")
    public ResponseEntity<List<String>> getAllMemberEmails() {
        List<String> memberEmails = memberService.getAllMemberEmails();
        return ResponseEntity.ok(memberEmails);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(adminService.getAllMembers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{email}/comments")
    public ResponseEntity<Map<String, List<?>>> getMemberComments(@PathVariable("email") String email) {
        return ResponseEntity.ok(adminService.getMemberComments(email));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{email}")
    public ResponseEntity<Member> getMemberDetails(@PathVariable("email") String email) {
        Member member = adminService.getMemberByEmail(email);
        return ResponseEntity.ok(member);
    }
}