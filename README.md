# Producer-Consumer Framework

### Table of contents
-----------------------

- Introduction
- Functionalities
- Requirements
- Framework Syntax
- Authors Information

### Introduction
------------------

The **Producer-Consumer** framework simplifies the task of exposing RESTful endpoints with implicit rules, it abstracts away the implementation of RESTful architecture from the *Plain Old Java Object*.



### Functionalities
----------------------

Following are the functionalities offered by **Producer-Consumer** framework:

- [x] Producer annotations to expose RESTful endpoints
- [x] Automatic documentation of exposed RESTful endpoints
- [ ] Consumer annotations to consume RESTful endpoints ( Work in Progress) 

### Requirements
------------------

The **Producer-Consumer** framework has following dependencies:

- Embedded tomcat

### Framework Syntax
----------------------

Following list provides detailed information on each of the annotations defined in this framework to expose and consume a RESTful endpoint.

#### Producer Annotations
- **@Producer**
  Annotation is used to mark a java class for automatic exposure of RESTful endpoints
  - Target - Class
  - Syntax
    ```java
    @Producer
    public class TestClass {
    
    public void method1(){
    
    }
    
    public void method2(){
    
    }
    
    }
    ```
    
- **@Expose**
  Annotation is used to mark a public method in a java class to be explicity exposed as a RESTful endpoint. When *@Expose* is used, automatic exposure of all public methods inside a java class is disabled and methods which are annotated with *@Expose* are given priority and exposed as RESTful endpoints.
  - Target - Method
  - Syntax
    ```java
    @Producer
    public class TestClass {
    
    @Expose
    public void method1(){
    
    }
    
    public void method2(){
    
    }
    
    }
    ```
  - Exposed RESTful endpoints
    - *method1* 


### Authors Information
-------------------------

- Suvandhana Nemani (@Suvandhana-Nemani)
  - suvandhana.nemani@gmail.com
- Naveen Kumar Shivarathri (@Naveen-Kumar-Shivarathri)
  - naveenkumarjn@outlook.com
 
 

