package org.bidtime.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class A implements Serializable {

  private Integer id;

  private String name;

  private Double salary;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getSalary() {
    return salary;
  }

  public void setSalary(Double salary) {
    this.salary = salary;
  }

}
