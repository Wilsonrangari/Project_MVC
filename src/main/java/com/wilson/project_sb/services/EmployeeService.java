package com.wilson.project_sb.services;


import com.wilson.project_sb.dto.EmployeeDTO;
import com.wilson.project_sb.entities.EmployeeEntity;
import com.wilson.project_sb.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public EmployeeService(EmployeeRepository employeeRepository, ModelMapper modelMapper){
       this.employeeRepository = employeeRepository;
       this.modelMapper = modelMapper;

   }

    public Optional<EmployeeDTO> getEmployeeById(Long employeeId) {
//        Optional<EmployeeEntity> employeeEntity = employeeRepository.findById(employeeId); //ctrl+alt+v
//        return employeeEntity.map(empEntity -> modelMapper.map(empEntity, EmployeeDTO.class));

        return employeeRepository.findById(employeeId).map(employee -> modelMapper.map(employee, EmployeeDTO.class));

    }

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().
                stream().
                map(employeeEntity -> modelMapper.map(employeeEntity, EmployeeDTO.class)).
                toList();
    }

    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        EmployeeEntity empEntity = modelMapper.map(employeeDTO, EmployeeEntity.class);
        EmployeeEntity savedEntity = employeeRepository.save(empEntity);
        return modelMapper.map(savedEntity, EmployeeDTO.class);

    }


    public EmployeeDTO updateEmployeeById(Long employeeId, EmployeeDTO employeeDTO) {
        EmployeeEntity employeeEntity = modelMapper.map(employeeDTO, EmployeeEntity.class);
        employeeEntity.setId(employeeId);
        EmployeeEntity savedEntity = employeeRepository.save(employeeEntity);
        return modelMapper.map(savedEntity, EmployeeDTO.class);
    }

    public Boolean isExistByEmployeeId(Long employeeId) {
        return employeeRepository.existsById(employeeId);
    }

    public boolean deleteEmployeeById(Long employeeId) {
           // employeeRepository.deleteById(employeeId);
        boolean exists = isExistByEmployeeId(employeeId);
        if(!exists) return false;
        employeeRepository.deleteById(employeeId);
        return true;


    }

    public EmployeeDTO updatePartialEmployeeById(Long employeeId, Map<String, Object> updates) {
        boolean exists = isExistByEmployeeId(employeeId);
        if(!exists) return null;
        EmployeeEntity employeeEntity = employeeRepository.findById(employeeId).get();
        updates.forEach(
                (field, value) -> {
                    Field fieldToBeUpdated = ReflectionUtils.findRequiredField(EmployeeEntity.class, field);
                    fieldToBeUpdated.setAccessible(true);
                    ReflectionUtils.setField(fieldToBeUpdated, employeeEntity, value);
                });
      return modelMapper.map(employeeRepository.save(employeeEntity), EmployeeDTO.class);
    }
}
