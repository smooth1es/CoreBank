package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(String id,
                          String fromAccountId,
                          String toAccountId,
                          BigDecimal amount,
                          LocalDateTime timestamp,
                          TransactionStatus status) {}
