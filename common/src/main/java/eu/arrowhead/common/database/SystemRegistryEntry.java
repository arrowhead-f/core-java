/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.common.database;

import eu.arrowhead.common.json.constraint.LDTInFuture;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "system_registry", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "arrowhead_system_id", "provider_device_id" }) })
public class SystemRegistryEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Valid
	@NotNull(message = "Provided ArrowheadSystem cannot be null")
	@JoinColumn(name = "arrowhead_system_id")
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@OnDelete(action = OnDeleteAction.CASCADE)
	private ArrowheadSystem providedSystem;

	@Valid
	@NotNull(message = "Provider ArrowheadDevice cannot be null")
	@JoinColumn(name = "provider_device_id")
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@OnDelete(action = OnDeleteAction.CASCADE)
	private ArrowheadDevice provider;

	@Column(name = "service_uri")
	@Size(max = 255, message = "Service URI must be 255 character at max")
	private String serviceUri;

	@Column(name = "end_of_validity")
  @LDTInFuture(message = "End of validity date cannot be in the past")
	private LocalDateTime endOfValidity;

	public SystemRegistryEntry() {
	}

	public SystemRegistryEntry(Long id,
                             @Valid @NotNull(message = "Provided ArrowheadSystem cannot be null") ArrowheadSystem providedSystem,
                             @Valid @NotNull(message = "Provider ArrowheadDevice cannot be null") ArrowheadDevice provider,
                             @Size(max = 255, message = "Service URI must be 255 character at max") String serviceUri,
                             @LDTInFuture(message = "End of validity date cannot be in the past") LocalDateTime endOfValidity) {
		this.id = id;
		this.providedSystem = providedSystem;
		this.provider = provider;
		this.serviceUri = serviceUri;
		this.endOfValidity = endOfValidity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArrowheadSystem getProvidedSystem() {
		return providedSystem;
	}

	public void setProvidedSystem(ArrowheadSystem providedSystem) {
		this.providedSystem = providedSystem;
	}

	public ArrowheadDevice getProvider() {
		return provider;
	}

	public void setProvider(ArrowheadDevice provider) {
		this.provider = provider;
	}

	public String getServiceUri() {
		return serviceUri;
	}

	public void setServiceUri(String serviceUri) {
		this.serviceUri = serviceUri;
	}

	public LocalDateTime getEndOfValidity() {
		return endOfValidity;
	}

	public void setEndOfValidity(LocalDateTime endOfValidity) {
		this.endOfValidity = endOfValidity;
	}

	@Override
	public int hashCode() {
		return Objects.hash(provider, providedSystem, serviceUri, endOfValidity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SystemRegistryEntry other = (SystemRegistryEntry) obj;

		return Objects.equals(this.provider, other.provider)
				&& Objects.equals(this.providedSystem, other.providedSystem)
				&& Objects.equals(this.serviceUri, other.serviceUri)
				&& Objects.equals(this.endOfValidity, other.endOfValidity);
	}
	
	protected void append(final StringBuilder builder)
	{
		builder.append("id=").append(id);
		builder.append(", providedSystem=").append(providedSystem);
		builder.append(", provider=").append(provider);
		builder.append(", serviceUri=").append(serviceUri);
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
}
