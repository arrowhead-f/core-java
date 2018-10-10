/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.core.deviceregistry.model;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import eu.arrowhead.common.database.ArrowheadDevice;

@Entity
@Table(name = "device_registry", uniqueConstraints = { @UniqueConstraint(columnNames = { "arrowhead_device_id" }) })
public class DeviceRegistryEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Valid
	@NotNull(message = "Provided ArrowheadDevice cannot be null")
	@JoinColumn(name = "arrowhead_device_id")
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@OnDelete(action = OnDeleteAction.CASCADE)
	private ArrowheadDevice providedDevice;

	@Column(name = "mac_address")
	@Size(max = 255, message = "macAddress must be 255 character at max")
	private String macAddress;

	@Column(name = "end_of_validity")
	@FutureOrPresent(message = "End of validity date cannot be in the past")
	private LocalDateTime endOfValidity;

	public DeviceRegistryEntry() {
		super();
	}

	public DeviceRegistryEntry(final Long id, 
			@Valid @NotNull(message = "Provided ArrowheadDevice cannot be null") final ArrowheadDevice providedDevice,
	    @Size(max = 255, message = "macAddress must be 255 character at max") final String macAddress,
	    @FutureOrPresent(message = "End of validity date cannot be in the past") final LocalDateTime endOfValidity) {
		super();
		this.id = id;
		this.providedDevice = providedDevice;
		this.macAddress = macAddress;
		this.endOfValidity = endOfValidity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArrowheadDevice getProvidedDevice() {
		return providedDevice;
	}

	public void setProvidedDevice(ArrowheadDevice providedDevice) {
		this.providedDevice = providedDevice;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public LocalDateTime getEndOfValidity() {
		return endOfValidity;
	}

	public void setEndOfValidity(LocalDateTime endOfValidity) {
		this.endOfValidity = endOfValidity;
	}
	
	protected void append(final StringBuilder builder)
	{
		builder.append("id=").append(id);
		builder.append(", providedDevice=").append(providedDevice);
		builder.append(", macAddress=").append(macAddress);
		builder.append(", endOfValidity=").append(endOfValidity);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [");
		append(builder);
		builder.append("]");
		return builder.toString();
	}	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceRegistryEntry other = (DeviceRegistryEntry) obj;

		return Objects.equals(this.providedDevice, other.providedDevice)
				&& Objects.equals(this.macAddress, other.macAddress)
				&& Objects.equals(this.endOfValidity, other.endOfValidity);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(providedDevice, macAddress, endOfValidity);
	}
}
