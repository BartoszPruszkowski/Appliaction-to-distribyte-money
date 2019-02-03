package pl.inz.costshare.mobile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.github.kittinunf.fuel.core.FuelError;
import pl.inz.costshare.mobile.dto.CreateUserDto;
import pl.inz.costshare.mobile.dto.UserDto;
import pl.inz.costshare.mobile.service.ResponseHandler;
import pl.inz.costshare.mobile.service.UserService;
import pl.inz.costshare.mobile.tools.Tools;

import java.util.regex.Pattern;

public class RegisterActivity extends Activity {

    private final Activity thisActivity = this;

    private UserService userService = new UserService();

    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            "(?=.*[a-z])" +         //at least 1 lower case letter
            "(?=.*[A-Z])" +         //at least 1 upper case letter
            "(?=\\S+$)" +           //no white spaces
            ".{8,}" +               //at least 8 characters
            "$");

    private static final Pattern NUMBER_PATTERN =
        Pattern.compile("^" +
            ".{9,}" +               //at least 9 characters
            "$");

    private TextInputLayout textInputName;
    private TextInputLayout textInputSurname;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputPasswordRepeated;
    private TextInputLayout textInputNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textInputEmail = findViewById(R.id.editTextEmail);
        textInputNumber = findViewById(R.id.editTextNumber);
        textInputName = findViewById(R.id.editTextFirstName);
        textInputSurname = findViewById(R.id.editTextLastName);
        textInputPassword = findViewById(R.id.editTextPassword);
        textInputPasswordRepeated = findViewById(R.id.editTextPasswordRepeated);

        findViewById(R.id.buttonCreateAcount).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                confirmInput();
            }
        });
    }

    private boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if (emailInput.isEmpty()) {
            textInputEmail.setError("Pole nie może być puste");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.setError("Proszę podać poprawny email");
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validateNumber() {
        String numberInput = textInputNumber.getEditText().getText().toString().trim();

        if (numberInput.isEmpty()) {
            textInputNumber.setError("Pole nie może być puste");
            return false;
        } else if (!NUMBER_PATTERN.matcher(numberInput).matches()) {
            textInputNumber.setError("Numer za krótki");
            return false;
        } else {
            textInputNumber.setError(null);
            return true;
        }
    }

    private boolean validateName() {
        String nameInput = textInputName.getEditText().getText().toString().trim();

        if (nameInput.isEmpty()) {
            textInputName.setError("Pole nie może być puste");
            return false;
        } else {
            textInputName.setError(null);
            return true;
        }
    }

    private boolean validateSurname() {
        String surnameInput = textInputSurname.getEditText().getText().toString().trim();

        if (surnameInput.isEmpty()) {
            textInputSurname.setError("Pole nie może być puste");
            return false;
        } else {
            textInputSurname.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Pole nie może być puste");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            textInputPassword.setError("Hasło za słabe");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    private boolean validatePasswordRepeated() {
        String passwordRepeatedInput = textInputPasswordRepeated.getEditText().getText().toString().trim();
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();

        if (passwordRepeatedInput.isEmpty()) {
            textInputPasswordRepeated.setError("Pole nie może być puste");
            return false;
        } else if (!passwordRepeatedInput.equals(passwordInput)) {
            textInputPasswordRepeated.setError("Hasło nie zgadza się");
            return false;
        } else {
            textInputPasswordRepeated.setError(null);
            return true;
        }
    }

    public void confirmInput() {
        if (!validateName() | !validateSurname() | !validateEmail() | !validateNumber() | !validatePassword() | !validatePasswordRepeated()) {
            return;
        }

        final String email = textInputEmail.getEditText().getText().toString().trim();
        final String number = textInputNumber.getEditText().getText().toString().trim();
        final String firstName = textInputName.getEditText().getText().toString().trim();
        final String lastName = textInputSurname.getEditText().getText().toString().trim();
        final String password = textInputPassword.getEditText().getText().toString().trim();
        final String psswordRepeated = textInputPasswordRepeated.getEditText().getText().toString().trim();

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setUserName(email);
        createUserDto.setPhoneNo(number);
        createUserDto.setFirstName(firstName);
        createUserDto.setLastName(lastName);
        createUserDto.setPassword(password);
        createUserDto.setPasswordRepeated(psswordRepeated);

        userService.createUser(createUserDto, new ResponseHandler<UserDto>(thisActivity) {
            @Override
            public void success(UserDto value) {
                Tools.info(getApplicationContext(), "Zarejestrowano pomyślnie!");
                finish();
            }

            @Override
            public void failure(FuelError error, String message) {
                Tools.error(getApplicationContext(), message);
            }
        });
    }
}
