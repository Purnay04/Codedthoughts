package com.codedthoughts.codedthoughts.views;

import com.codedthoughts.codedthoughts.constants.RegexpConstants;
import com.codedthoughts.codedthoughts.enums.Role;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestView {

//    @Pattern(regexp = RegexpConstants.USERNAME_REGEX, message = "Please re-enter the User Name with only Alphabets, Number & only Special Character As _ !")
    private String username;

//    @Min(value = 8)
//    @Pattern(regexp = RegexpConstants.PASSWORD_REGEX, message = "Please re-enter the Password!")
    private String password;

    private String email;
    private Date DOB;

    @Override
    public String toString() {
        return "SignupRequestView{" +
                "userName='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", DOB=" + DOB +
                '}';
    }
}
