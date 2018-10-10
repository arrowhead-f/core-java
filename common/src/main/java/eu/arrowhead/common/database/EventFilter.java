/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.common.database;

import com.google.common.base.MoreObjects;
import eu.arrowhead.common.json.constraint.LDTInFuture;
import eu.arrowhead.common.json.constraint.SENotBlank;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "event_filter", uniqueConstraints = {@UniqueConstraint(columnNames = {"event_type", "consumer_system_id"})})
public class EventFilter {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank
  @Column(name = "event_type")
  @Size(max = 255, message = "Event type must be 255 character at max")
  private String eventType;

  @Valid
  @NotNull(message = "Consumer ArrowheadSystem cannot be null")
  @JoinColumn(name = "consumer_system_id")
  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ArrowheadSystem consumer;

  @Valid
  @Size(max = 100, message = "Event filter can only have 100 sources at max")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "event_filter_sources_list", joinColumns = @JoinColumn(name = "filter_id"))
  private Set<ArrowheadSystem> sources = new HashSet<>();

  @Column(name = "start_date")
  private LocalDateTime startDate;

  @Column(name = "end_date")
  @LDTInFuture(message = "Filter end date must be in the future")
  private LocalDateTime endDate;

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "metadata_key")
  @Column(name = "metadata_value", length = 2047)
  @CollectionTable(name = "event_filter_metadata", joinColumns = @JoinColumn(name = "filter_id"))
  private Map<@SENotBlank String, @SENotBlank String> filterMetadata = new HashMap<>();

  @Column(name = "notify_uri")
  private String notifyUri;

  //TODO provide a REST interface to easily switch this
  @Column(name = "match_metadata")
  @Type(type = "yes_no")
  private Boolean matchMetadata = false;

  public EventFilter() {
  }

  public EventFilter(String eventType, ArrowheadSystem consumer, Set<ArrowheadSystem> sources, LocalDateTime startDate, LocalDateTime endDate,
                     Map<String, String> filterMetadata, String notifyUri, boolean matchMetadata) {
    this.eventType = eventType;
    this.consumer = consumer;
    this.sources = sources;
    this.startDate = startDate;
    this.endDate = endDate;
    this.filterMetadata = filterMetadata;
    this.notifyUri = notifyUri;
    this.matchMetadata = matchMetadata;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  public ArrowheadSystem getConsumer() {
    return consumer;
  }

  public void setConsumer(ArrowheadSystem consumer) {
    this.consumer = consumer;
  }

  public Set<ArrowheadSystem> getSources() {
    return sources;
  }

  public void setSources(Set<ArrowheadSystem> sources) {
    this.sources = sources;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDateTime startDate) {
    this.startDate = startDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDateTime endDate) {
    this.endDate = endDate;
  }

  public Map<String, String> getFilterMetadata() {
    return filterMetadata;
  }

  public void setFilterMetadata(Map<String, String> filterMetadata) {
    this.filterMetadata = filterMetadata;
  }

  public String getNotifyUri() {
    return notifyUri;
  }

  public void setNotifyUri(String notifyUri) {
    this.notifyUri = notifyUri;
  }

  public Boolean isMatchMetadata() {
    return matchMetadata;
  }

  public void setMatchMetadata(Boolean matchMetadata) {
    this.matchMetadata = matchMetadata;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EventFilter)) {
      return false;
    }
    EventFilter that = (EventFilter) o;
    return Objects.equals(eventType, that.eventType) && Objects.equals(consumer, that.consumer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventType, consumer);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("eventType", eventType).add("consumer", consumer).toString();
  }
}
