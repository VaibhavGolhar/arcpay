import { create } from 'zustand';
import { Payment, PaymentStore } from '../types/payment';

export const usePaymentStore = create<PaymentStore>((set, get) => ({
  payments: [],
  addPayment: (payment) => {
    const newPayment: Payment = {
      ...payment,
      id: Date.now().toString(),
      timestamp: Date.now(),
      syncStatus: 'pending',
    };
    set((state) => ({
      payments: [...state.payments, newPayment],
    }));
  },
  updatePaymentStatus: (id, status) => {
    set((state) => ({
      payments: state.payments.map((payment) =>
        payment.id === id ? { ...payment, status } : payment
      ),
    }));
  },
  getPaymentById: (id) => {
    return get().payments.find((payment) => payment.id === id);
  },
}));