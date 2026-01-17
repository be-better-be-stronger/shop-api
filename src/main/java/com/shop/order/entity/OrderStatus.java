package com.shop.order.entity;

public enum OrderStatus {
    PLACED {
        @Override
        public boolean canCancel() { return true; }
    },
    PACKED {
        @Override
        public boolean canCancel() { return false; }
    },
    SHIPPED {
        @Override
        public boolean canCancel() { return false; }
    },
    DELIVERED {
        @Override
        public boolean canCancel() { return false; }
    },
    CANCELED {
        @Override
        public boolean canCancel() { return false; }
    };

    public abstract boolean canCancel();
}
