# SERVER HP

### TEACHERS: 
* Porta, Enrique
* Ripani, Luciano

### TEAM MEMBERS
* Pellicciotti, Maximiliano - 36244
* Vallejo, Federico - 41886
* Villasanri, Braian - 45517
* Tabasso, Nahuel - 43204

# PROJECT DESCRIPTION

Server for the Hospital System for the final practical work of the "Habilitacion Profesional" subject.
Project that concentrates all the security module of the system and the business Core.

### GETTING STARTED
Installer the follow software in your SO (Windows, Linux, MacOS)
* Maven 3.5+
* Java 17 (JDK -> Java Development Kit)
* MySQL 8

### SET UP OF LOCAL ENVIRONMENT
To lift the local environment

~~~~
- git clone <repository>
- git checkout develop
- git pull origin develop
- Import the project into a IDE workspace (Intellij IDEA or Eclipse for Enterprise Develop + Spring Tool Suite 4 Plugin)
- Put such as active profile a local profile in Settings (Run\Debug Config) with this command in VM -Dspring.profiles.active=local
- Run the Spring Boot Project
- Test with this path http://localhost:8080/api/**
~~~~

If you use a Eclipse such as your IDE, for import a maven project follow this steps
File -> Import -> General -> Existing Maven Projects

### SET UP DATABASE

If you want to use a local MySQL database you need to create a MySQL Connection
With this data

~~~~
- User: root
- Password: root123-
- Database: hospital_local
~~~~

If you want to use a cloud MySQL database you need the connection data por open connection

### PROJECT DOCUMENTATION

[Google Drive](https://drive.google.com/drive/folders/1pZJ_npawBoQQzBTLG16dGug_5HctNYR1)




