package ru.sberbank.reboot.bank.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.sberbank.reboot.bank.refs.Currency;
import ru.sberbank.reboot.bank.refs.Status;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Сущность счет
 */
@Data
@Entity
@Table(name = "ACCOUNT")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Account {

    @Id
    @GeneratedValue
    private Long id;
    private Long number;
    private String fullNumber;

    //Счет первого порядка
    //по умолчанию для физлиц
    private int firstOrderNumber = 408;

    //Счет второго порядка
    //по умолчанию текущие и карточные счета
    private String secondOrderAccNumber = "17";

    //Номер филиала банка
    private String branch = "0000";

    //Контрольная цифра
    private int controlDigit = 5;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private String currencyCode;

    private BigDecimal balance = BigDecimal.valueOf(0);

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<BankTransaction> transactionList;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdDateTime;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDateTime;

    @Override
    public String toString() {
        return "Account{" +
          "id=" + id +
          ", number=" + number +
          ", fullNumber='" + fullNumber + '\'' +
          ", firstOrderNumber=" + firstOrderNumber +
          ", secondOrderAccNumber='" + secondOrderAccNumber + '\'' +
          ", branch='" + branch + '\'' +
          ", controlDigit=" + controlDigit +
          ", currency=" + currency +
          ", currencyCode='" + currencyCode + '\'' +
          ", balance=" + balance +
          ", status=" + status +
          ", createdDateTime=" + createdDateTime +
          ", updatedDateTime=" + updatedDateTime +
          '}';
    }
}
