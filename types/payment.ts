export interface Payment {
  id: string;
  amount: number;
  description: string;
  recipient: string;
  timestamp: number;
  status: 'pending' | 'completed' | 'failed';
  syncStatus: 'pending' | 'synced' | 'failed';
}

export interface PaymentStore {
  payments: Payment[];
  addPayment: (payment: Omit<Payment, 'id' | 'timestamp' | 'syncStatus'>) => void;
  updatePaymentStatus: (id: string, status: Payment['status']) => void;
  getPaymentById: (id: string) => Payment | undefined;
}