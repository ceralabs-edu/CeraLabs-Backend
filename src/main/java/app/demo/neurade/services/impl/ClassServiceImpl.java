package app.demo.neurade.services.impl;

import app.demo.neurade.domain.dtos.request.ClassCreationRequest;
import app.demo.neurade.domain.models.Classroom;
import app.demo.neurade.domain.models.User;
import app.demo.neurade.repositories.ClassRepository;
import app.demo.neurade.services.ClassService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;

    @Override
    @Transactional
    public Classroom createClass(User creator, ClassCreationRequest req) {
        Classroom clazz = Classroom.builder()
                .name(req.getName())
                .creator(creator)
                .build();

        return classRepository.save(clazz);
    }
}
