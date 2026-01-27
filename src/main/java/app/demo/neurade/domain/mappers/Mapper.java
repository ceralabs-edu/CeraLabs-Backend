package app.demo.neurade.domain.mappers;

import app.demo.neurade.domain.dtos.*;
import app.demo.neurade.domain.dtos.externals.responses.ExtVerifyKeyResponse;
import app.demo.neurade.domain.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {
    public UserDTO toDto(User user) {
        return UserDTO.builder()
                .email(user.getEmail())
                .verified(user.getVerified())
                .build();
    }

    public UserInfoDTO toDto(UserInformation userInformation) {
        return UserInfoDTO.builder()
                .firstName(userInformation.getFirstName())
                .lastName(userInformation.getLastName())
                .cityCode(userInformation.getCity().getCode())
                .subDistrictCode(userInformation.getSubDistrict().getCode())
                .addressDetail(userInformation.getAddressDetail())
                .school(userInformation.getSchool())
                .grade(userInformation.getGrade())
                .bio(userInformation.getBio())
                .favoriteSubjects(userInformation.getFavoriteSubjects())
                .dateOfBirth(userInformation.getDateOfBirth())
                .avatarUrl(userInformation.getAvatarImage())
                .build();
    }

    public ClassDTO toDto(Classroom classroom) {
        return ClassDTO.builder()
                .classId(classroom.getId())
                .name(classroom.getName())
                .creatorId(classroom.getCreator().getId())
                .description(classroom.getDescription())
                .build();
    }

    public ValidateKeyDTO toDto(ExtVerifyKeyResponse response) {
        return ValidateKeyDTO.builder()
                .isValid(response.isValid())
                .models(response.getModels())
                .errorMessage(response.getErrorMessage())
                .build();
    }

    public AIPackageInstanceDTO toDto(AIPackageInstance aiPackageInstance, AIPackage aiPackage) {
        return AIPackageInstanceDTO.builder()
                .instanceId(aiPackageInstance.getId())
                .aiPackageId(aiPackageInstance.getAiPackage().getId())
                .classId(aiPackageInstance.getClassRoom().getId())
                .purchaserId(aiPackageInstance.getBuyer().getId())
                .remainingToken(aiPackageInstance.getRemainingToken())
                .totalToken(aiPackage.getTotalToken())
                .purchaseDate(aiPackageInstance.getPurchaseDate())
                .expiryDate(aiPackageInstance.getExpiryDate())
                .build();
    }

    public AIPackageDTO toDto(AIPackage aiPackage) {
        return AIPackageDTO.builder()
                .id(aiPackage.getId())
                .name(aiPackage.getName())
                .price(aiPackage.getPrice())
                .totalToken(aiPackage.getTotalToken())
                .tokenRateLimit(aiPackage.getTokenRateLimit())
                .model(aiPackage.getModel())
                .durationInDays(aiPackage.getDurationInDays())
                .description(aiPackage.getDescription())
                .build();
    }
}
