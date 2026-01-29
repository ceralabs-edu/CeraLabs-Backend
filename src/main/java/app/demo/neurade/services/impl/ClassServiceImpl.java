package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.requests.ClassCreationRequest;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.infrastructures.repositories.ClassRepository;
import app.demo.neurade.services.ClassService;
import app.demo.neurade.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Classroom createClass(User creator, ClassCreationRequest req) {
        Classroom clazz = Classroom.builder()
                .name(req.getClassName())
                .description(req.getDescription())
                .creator(creator)
                .build();

        return classRepository.save(clazz);
    }

    @Override
    public List<Classroom> getAllClassesUnderManagement(User manager) {
        List<User> managedUsers = userService.getUsersUnderManagement(manager);
        return classRepository.findByCreatorIn(managedUsers);
    }

    @Override
    public Classroom getClass(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class with ID " + classId + " not found"));
    }
}
