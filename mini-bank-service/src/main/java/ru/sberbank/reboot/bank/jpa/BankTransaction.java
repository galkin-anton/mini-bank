package ru.sberbank.reboot.bank.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 * Сущность транзакции
 */
@Data
@Entity
public class BankTransaction {
    @Id
    @GeneratedValue
    private Long id;
    //Счет отправителя
    @Column(nullable = false)
    private Long sourceAccount;
    //Счет получателя
    @Column(nullable = false)
    private Long targetAccount;
    //Сумма до операции на счете источнике
    @Column(nullable = false)
    private BigDecimal balanceSourceBefore;
    //Сумма после операции на счете источнике
    @Column(nullable = false)
    private BigDecimal balanceSourceAfter;
    //Сумма до операции на счете источнике
    @Column(nullable = false)
    private BigDecimal balanceTargetBefore;
    //Сумма после операции на счете источнике
    @Column(nullable = false)
    private BigDecimal balanceTargetAfter;
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Account> accountList;
    //Сумма операции
    @Column(nullable = false)
    private BigDecimal operationSumm;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date createdDateTime;

    @Override
    public String toString() {
        return "BankTransaction{" +
          "id=" + id +
          ", sourceAccount=" + sourceAccount +
          ", targetAccount=" + targetAccount +
          ", balanceSourceBefore=" + balanceSourceBefore +
          ", balanceSourceAfter=" + balanceSourceAfter +
          ", balanceTargetBefore=" + balanceTargetBefore +
          ", balanceTargetAfter=" + balanceTargetAfter +
          ", operationSumm=" + operationSumm +
          ", createdDateTime=" + createdDateTime +
          '}';
    }
}
