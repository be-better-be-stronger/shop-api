package com.shop.common.upload;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public final class Tx {
  private Tx() {}

  public static void afterCommit(Runnable r) {
    if (!TransactionSynchronizationManager.isSynchronizationActive()) {
      r.run();
      return;
    }
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override public void afterCommit() { r.run(); }
    });
  }
}
