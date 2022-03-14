package uz.pdp.appjparelationships.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    private final AddressRepository addressRepository;
    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return studentRepository.findAllByGroup_Faculty_Id(facultyId, pageable);
    }

    //4. GROUP OWNER
    @GetMapping("/forGroupOwner/{groupId}")
    public Page<Student> getStudentListForGroupOwner(@PathVariable Integer groupId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        return studentRepository.findAllByGroup_Id(groupId, pageable);
    }

    @PostMapping("/add")
    public String add(@RequestBody StudentDto studentDto) {
        Address address = new Address();
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());
        Address savedAddress = addressRepository.save(address);

        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        if (optionalGroup.isPresent()) {
            Student student = new Student();
            student.setAddress(savedAddress);
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            student.setGroup(optionalGroup.get());
            List<Subject> subjects = new ArrayList<>();
            for (Integer id : studentDto.getSubjectsId()) {
                Optional<Subject> optionalSubject = subjectRepository.findById(id);
                optionalSubject.ifPresent(subjects::add);
            }
            student.setSubjects(subjects);
            studentRepository.save(student);
            return "Student successfully added";
        } else
            return "Group not found";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            Integer addressId = student.getAddress().getId();
            studentRepository.deleteById(id);
            addressRepository.deleteById(addressId);
        }
        return "Successfully deleted";
    }

    @PutMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, @RequestBody StudentDto studentDto){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()){
            Address address = new Address();
            address.setId(optionalStudent.get().getAddress().getId());
            address.setCity(studentDto.getCity());
            address.setDistrict(studentDto.getDistrict());
            address.setStreet(studentDto.getStreet());
            Address savedAddress = addressRepository.save(address);

            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
            if (optionalGroup.isPresent()){
                Student student = new Student();
                student.setId(optionalStudent.get().getId());
                student.setFirstName(studentDto.getFirstName());
                student.setLastName(studentDto.getLastName());
                student.setAddress(savedAddress);
                student.setGroup(optionalGroup.get());
                List<Subject> subjects = new ArrayList<>();
                for (Integer subjectId : studentDto.getSubjectsId()) {
                    Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
                    optionalSubject.ifPresent(subjects::add);
                }
                student.setSubjects(subjects);
                studentRepository.save(student);
//                addressRepository.deleteById(optionalStudent.get().getAddress().getId());
                return "Successfully edited";
            }else
                return "Group not found";
        }
        return "Student not found";
    }

}
