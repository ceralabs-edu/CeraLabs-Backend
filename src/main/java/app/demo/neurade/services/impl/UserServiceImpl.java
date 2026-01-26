package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.request.PatchUserRequest;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.domain.models.UserInformation;
import app.demo.neurade.repositories.UserInformationRepository;
import app.demo.neurade.repositories.UserRepository;
import app.demo.neurade.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserInformationRepository infoRepository;

    @Override
    @Transactional
    public UserInformation updateUserInfo(String email, PatchUserRequest req) {
        UserInformation info = infoRepository.findByUserEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User information not found"));

        BeanUtils.copyProperties(req, info, getNullPropertyNames(req));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return infoRepository.save(info);
    }

    private String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        return Arrays.stream(src.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }
}
