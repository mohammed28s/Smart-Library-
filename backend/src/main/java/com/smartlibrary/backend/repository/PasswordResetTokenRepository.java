package com.smartlibrary.backend.repository;

import com.smartlibrary.backend.entity.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndUsedAtIsNull(String token);

    Optional<PasswordResetToken> findFirstByUserPhoneAndTokenAndUsedAtIsNullOrderByCreatedAtDesc(
            String phone, String token);
}
