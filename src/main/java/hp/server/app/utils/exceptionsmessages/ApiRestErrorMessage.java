package hp.server.app.utils.exceptionsmessages;

public class ApiRestErrorMessage {

    public static final String FIRSTNAME_OR_LASTNAME_CONTAINS_DIGITS = "El nombre y el apellido no pueden contener caracteres numericos";
    public static final String BIRTH_DATE_INVALID = "La fecha de nacimiento no puede ser posterior a hoy";
    public static final String PERSON_EXISTS_BY_DOCUMENT = "El DNI del usuario ya existe";
    public static final String PHONE_NUMBER_INVALID = "El telefono no puede contener caracteres alfabeticos";
    public static final String EXISTS_PERSON_BY_EMAIL = "Existe un usuario registrado con el email: ";
    public static final String CITY_INVALID = "La localidad no existe";
}
