package uz.pdp.appjparelationships.payload;

import lombok.Data;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Subject;

import javax.persistence.Column;
import java.util.List;

@Data
public class StudentDto {
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // address
    private String city;
    private String district;
    private String street;

    private Integer groupId;

    private List<Integer> subjectsId;

}
