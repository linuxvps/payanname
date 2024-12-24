package org.knn.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "TERMINAL")
@Data
public class Terminal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ASSET_NUMBER", length = 30)
    private String assetNumber;

    @Column(name = "MAC_KEY", length = 48)
    private String macKey;

    @Column(name = "MANUFACTURER", length = 2)
    private String manufacturer;

    @Column(name = "PIN_KEY", length = 48)
    private String pinKey;

    @Column(name = "SERIAL_NO", length = 30)
    private String serialNo;

    @Column(name = "TERMINAL_MODEL", length = 2)
    private String terminalModel;

    @Column(name = "TERMINAL_NUMBER", unique = true)
    private Long terminalNumber;

    @Column(name = "TERMINAL_STATUS", length = 1)
    private String terminalStatus;

    @Column(name = "TERMINAL_TYPE", length = 2)
    private String terminalType;

    @Column(name = "ACQUIRER_ID")
    private Long acquirer;

    @Column(name = "ROWUPDSEQ")
    private Long rowUpdSeq;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INSERT_SYSDATE")
    private Date insertSysdate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_SYSDATE")
    private Date updateSysdate;

    @Column(name = "UPDATE_USER", length = 32)
    private String updateUser;

    @Column(name = "INSERT_USER", length = 32)
    private String insertUser;

    @Column(name = "TIME_STAMP")
    private Date timeStamp;

    @Column(name = "BATCH_ID", nullable = false)
    private Long batchId = 0L;

    @Column(name = "IP_ADDRESS", length = 15)
    private String ipAddress;

    @Column(name = "MASTER_KEY", length = 48)
    private String masterKey;

    @Column(name = "PERSIAN_TITLE", length = 40)
    private String persianTitle = "NO TITLE";

    @Column(name = "ENGLISH_TITLE", length = 40)
    private String englishTitle = "NO TITLE";

    @Column(name = "TERMINAL_GROUP_ID")
    private Long terminalGroupId;

    @Column(name = "TRACE_ID", columnDefinition = "DECIMAL(10,0) default 0")
    private Long traceId = 0L;

    @Column(name = "LATITUDE", columnDefinition = "DECIMAL(5, 7) default 0.0")
    private Double latitude = 0.0;

    @Column(name = "LONGITUDE", columnDefinition = "DECIMAL(5, 7) default 0.0")
    private Double longitude = 0.0;

}
