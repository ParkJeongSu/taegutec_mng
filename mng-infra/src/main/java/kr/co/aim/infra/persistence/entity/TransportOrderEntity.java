package kr.co.aim.infra.persistence.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "TRANSPORT_ORDER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransportOrderEntity {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "TRANSPORT_ORDER_NAME", length = 40, nullable = false)
    private String transportOrderName;

    @Column(name = "DESCRIPTION", length = 40)
    private String description;

    @Column(name = "TRANSPORT_TYPE", length = 40)
    private String transportType;

    @Column(name = "TRANSPORT_ORDER_ID", length = 40)
    private String transportOrderId;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "GAL_ID", length = 40)
    private String galId;

    @Column(name = "GAL_WAREHOUSE", length = 40)
    private String galWarehouse;

    @Column(name = "FROM_WAREHOUSE", length = 40)
    private String fromWarehouse;

    @Column(name = "FROM_ZONE_NAME", length = 40)
    private String fromZoneName;

    @Column(name = "FROM_LOCATION_ID", length = 40)
    private String fromLocationId;

    @Column(name = "TO_WAREHOUSE", length = 40)
    private String toWarehouse;

    @Column(name = "TO_ZONE_NAME", length = 40)
    private String toZoneName;

    @Column(name = "TO_LOCATION_ID", length = 40)
    private String toLocationId;

    @Column(name = "CARRIER_NAME", length = 40)
    private String carrierName;

    @Column(name = "CARRIER_TYPE", length = 40)
    private String carrierType;

    @Column(name = "DRIVING_PROFILE", length = 40)
    private String drivingProfile;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "RELEASE_TIME")
    private LocalDateTime releaseTime;

    @Column(name = "COMPLETE_TIME")
    private LocalDateTime completeTime;

    @Column(name = "CREATE_USER", length = 40)
    private String createUser;

    @Column(name = "RELEASE_USER", length = 40)
    private String releaseUser;

    @Column(name = "COMPLETE_USER", length = 40)
    private String completeUser;
}