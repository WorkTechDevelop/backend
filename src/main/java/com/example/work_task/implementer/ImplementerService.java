package com.example.work_task.implementer;

import com.example.work_task.model.ImplementerModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ImplementerService {

    private final ImplementerRepository implementerRepository;

    public void createImplementer(ImplementerModel implementerModel) {
    }
}
