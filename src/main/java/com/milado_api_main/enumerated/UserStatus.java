package com.milado_api_main.enumerated;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
	
	PENDING_APPROVAL("Pending Approval", List.of("userprofile.read", "userprofile.update", "user-identity.verify")),
	APPROVED("Approved", List.of("full-access")),
	DEACTIVATED("Deactivated", List.of("userprofile.read"));
	
	private final String value;
	private final List<String> scopes;

}
