package com.apiexamples.service;

import com.apiexamples.entity.Registration;
import com.apiexamples.exception.ResourceNotFound;
import com.apiexamples.payload.RegistrationDto;
import com.apiexamples.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    private RegistrationRepository registrationRepository;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public RegistrationServiceImpl(){

    }

    @Override

    public RegistrationDto createRegistration(RegistrationDto registrationDto) {

        Registration registration = mapToEntity(registrationDto);
        Registration savedEntity = registrationRepository.save(registration);
        RegistrationDto dto = mapToDto(savedEntity);
        dto.setMessage("Registration Saved");
        return dto;
    }

    @Override
    public void deleteRegistrationById(long id) {
        registrationRepository.deleteById(id);
    }

    @Override
    public RegistrationDto updateRegistration(long id, RegistrationDto registrationDto) {
        Optional<Registration> opReg = registrationRepository.findById(id);
        Registration registration = opReg.get();

        registration.setName(registrationDto.getName());
        registration.setEmail(registrationDto.getEmail());
        registration.setMobile(registrationDto.getMobile());
        Registration savedEntity = registrationRepository.save(registration);
        RegistrationDto dto = mapToDto(registration);
        return dto;
    }

    @Override
    public List<RegistrationDto> getAllRegistrations(int pageNo, int pageSize, String sortBy, String sortDir) {
        //List<Registration> registrations = registrationRepository.findAll();
        //Ternary Operator
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(Sort.Direction.ASC,sortBy): Sort.by(Sort.Direction.DESC,sortBy);
        Pageable pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Registration> all = registrationRepository.findAll(pageable);
        List<Registration> registrations = all.getContent();
        RegistrationServiceImpl r = new RegistrationServiceImpl();
        List<RegistrationDto>registrationDtos=registrations.stream().map(r::mapToDto).collect(Collectors.toList());
        System.out.println(all.getTotalPages());//returns total no.of pages our table consist
        System.out.println(all.isLast());//says whether it reached the last page or not(true/False)
        System.out.println(all.isFirst());//returns true if in first page
        System.out.println(pageable.getPageSize());//returns how many elements are their in perticular page
        System.out.println(pageable.getPageNumber());//returns page no. on which you are in
        return registrationDtos;
    }

    @Override
    public RegistrationDto getRegistrationById(long id) {
        Registration registration = registrationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFound("Registration not found with id:" + id)
        );
        RegistrationDto registrationDto = mapToDto(registration);
        return registrationDto;
    }
    //url=http://localhost:8080/api/v1/registration?pageNo=0&pageSize=5&sortBy=email&sortDir=desc

    Registration mapToEntity(RegistrationDto dto) {
        Registration entity = new Registration();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setMobile(dto.getMobile());
        return entity;
    }

    RegistrationDto mapToDto(Registration registration) {
        RegistrationDto dto = new RegistrationDto();
        dto.setId(registration.getId());
        dto.setName(registration.getName());
        dto.setEmail(registration.getEmail());
        dto.setMobile(registration.getMobile());
        return dto;
    }
}