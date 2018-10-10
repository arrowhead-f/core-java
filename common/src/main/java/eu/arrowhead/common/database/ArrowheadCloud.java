/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.common.database;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "arrowhead_cloud", uniqueConstraints = {@UniqueConstraint(columnNames = {"operator", "cloud_name"})})
public class ArrowheadCloud {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank
  @Length(max = 255, message = "Cloud operator must be 255 character at max")
  @Pattern(regexp = "[A-Za-z0-9-_:]+", message = "Cloud operator can only contain alphanumerical characters and some special characters (dash, "
      + "underscore and colon)")
  private String operator;

  @NotBlank
  @Column(name = "cloud_name")
  @Size(max = 255, message = "Cloud name must be 255 character at max")
  @Pattern(regexp = "[A-Za-z0-9-_:]+", message = "Cloud name can only contain alphanumerical characters and some special characters (dash, "
      + "underscore and colon)")
  private String cloudName;

  @NotBlank
  @Size(min = 3, max = 255, message = "Cloud address must be between 3 and 255 characters")
  private String address;

  @NotNull
  @Min(value = 1, message = "Port can not be less than 1")
  @Max(value = 65535, message = "Port can not be greater than 65535")
  private Integer port;

  @NotBlank
  @Column(name = "gatekeeper_service_uri")
  private String gatekeeperServiceURI;

  @Column(name = "authentication_info")
  @Size(max = 2047, message = "Authentication information must be 2047 character at max")
  private String authenticationInfo;

  @Column(name = "is_secure")
  @Type(type = "yes_no")
  private Boolean secure = false;

  public ArrowheadCloud() {
  }

  public ArrowheadCloud(String operator, String cloudName, String address, Integer port, String gatekeeperServiceURI, String authenticationInfo,
                        Boolean secure) {
    this.operator = operator;
    this.cloudName = cloudName;
    this.address = address;
    this.port = port;
    this.gatekeeperServiceURI = gatekeeperServiceURI;
    this.authenticationInfo = authenticationInfo;
    this.secure = secure;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getCloudName() {
    return cloudName;
  }

  public void setCloudName(String cloudName) {
    this.cloudName = cloudName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getGatekeeperServiceURI() {
    return gatekeeperServiceURI;
  }

  public void setGatekeeperServiceURI(String gatekeeperServiceURI) {
    this.gatekeeperServiceURI = gatekeeperServiceURI;
  }

  public String getAuthenticationInfo() {
    return authenticationInfo;
  }

  public void setAuthenticationInfo(String authenticationInfo) {
    this.authenticationInfo = authenticationInfo;
  }

  public Boolean isSecure() {
    return secure;
  }

  public void setSecure(Boolean secure) {
    this.secure = secure;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ArrowheadCloud)) {
      return false;
    }
    ArrowheadCloud that = (ArrowheadCloud) o;
    return Objects.equals(operator, that.operator) && Objects.equals(cloudName, that.cloudName) && Objects.equals(address, that.address) && Objects
        .equals(port, that.port) && Objects.equals(gatekeeperServiceURI, that.gatekeeperServiceURI) && Objects.equals(secure, that.secure);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operator, cloudName, address, port, gatekeeperServiceURI, secure);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("operator", operator).add("cloudName", cloudName).add("address", address).add("port", port)
                      .add("gatekeeperServiceURI", gatekeeperServiceURI).add("secure", secure).toString();
  }

  public void partialUpdate(ArrowheadCloud other) {
    this.operator = other.getOperator() != null ? other.getOperator() : this.operator;
    this.cloudName = other.getCloudName() != null ? other.getCloudName() : this.cloudName;
    this.address = other.getAddress() != null ? other.getAddress() : this.address;
    this.port = other.getPort() != null ? other.getPort() : this.port;
    this.gatekeeperServiceURI = other.getGatekeeperServiceURI() != null ? other.getGatekeeperServiceURI() : this.gatekeeperServiceURI;
    this.authenticationInfo = other.getAuthenticationInfo() != null ? other.getAuthenticationInfo() : this.authenticationInfo;
    this.secure = other.isSecure() != null ? other.isSecure() : this.secure;
  }
}