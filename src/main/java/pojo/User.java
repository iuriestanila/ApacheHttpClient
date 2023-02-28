package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int age;
    private String name;
    private String sex;
    private String zipCode;

    public User(int age, String sex, String zipCode) {
        this.age = age;
        this.sex = sex;
        this.zipCode = zipCode;
    }
}
