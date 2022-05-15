# Producer-Consumer Framework

### Table of contents
-----------------------

- Introduction
- Functionalities
- Requirements
- Assumptions and Rules
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

JDK requirements:

- Compiler compliance level is greater than **1.8**
- enable store information about method parameters is enabled

### Assumptions and Rules
---------------------------

Following assumptions and rules are considered during the initial development of this framework. Changes are always welcome, please write your best assumptions to authors!

The assumptions are exclusively made on **Primitive** data types, **'String, char, int , short, long, float, decimal, boolean'**.

The rules given below determine preferred HTTP method for a given java method.

#### GET Method rules:
- There is a single parameter in the method signature.
- The single parameter belongs to **'String, char, int, short,boolean'**.
- Other than the above defined primitive types, if a method includes other parameter types which are not primitive and doesn't belong to above defined primitive types, the method is eligible for **POST** or **PUT** HTTP Method.
- If **@NoConceal** annotation is used, more than one parameter is allowed in method signature and the method is eligible for **GET** HTTP Method.
- If parameter is annotated with **@Headers** or parameter is a type of **HttpServletRequest** or **HttpServletResponse**, then, that parameter is not accounted while resolving methods for an appropriate HTTP method. Hence, if a method includes a primitive **GET** type primitive parameter and a complex parameter annotated with **@Headers** or parameter is a type of **HttpServletRequest** or **HttpServletResponse**, the complex parameter is not taken into account.

#### PUT Method rules:
- In the method signature, if the method name contains **'update'** string, the method is eligible for **'PUT'** HTTP Method.
- Complex type parameters are allowed, all complex parameters are included in the request body of **'PUT'** request. 

#### DELETE Method rules:
- In the method signature, if the method name contains **'delete, remove, purge or close'** String, the method is eligible for **'DELETE'** HTTP method.
- Complex type parameters are not allowed, if complex parameters are defined in the method signature, the method is eligible for **'POST'** HTTP method.

#### POST Method Rules:
- A method is eligible for **'POST'**, if it is not eligible for **'GET,PUT,DELETE'**. 
- If method signature includes complex parameters, the method is eligible for **'POST'** HTTP Method.
- If parameters defined in method signature belongs to **'long, decimal, float'**, the method is eligible for **'POST'** HTTP Method.

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
 
 

