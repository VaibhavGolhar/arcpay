import React from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';
import { usePaymentStore } from '../store/paymentStore';
import { Payment } from '../types/payment';

export function RecentTransactions() {
  const payments = usePaymentStore((state) => state.payments);

  const renderTransaction = ({ item }: { item: Payment }) => (
    <View style={styles.transactionItem}>
      <View style={styles.transactionLeft}>
        <Text style={styles.recipient}>{item.recipient}</Text>
        <Text style={styles.description}>{item.description}</Text>
      </View>
      <View style={styles.transactionRight}>
        <Text style={styles.amount}>
          ${item.amount.toFixed(2)}
        </Text>
        <View style={[
          styles.statusBadge,
          { backgroundColor: getStatusColor(item.status) }
        ]}>
          <Text style={styles.statusText}>
            {item.status.charAt(0).toUpperCase() + item.status.slice(1)}
          </Text>
        </View>
      </View>
    </View>
  );

  const getStatusColor = (status: Payment['status']) => {
    switch (status) {
      case 'completed':
        return '#d4edda';
      case 'pending':
        return '#fff3cd';
      case 'failed':
        return '#f8d7da';
      default:
        return '#f8f9fa';
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Recent Transactions</Text>
      <FlatList
        data={payments.slice(0, 5)}
        renderItem={renderTransaction}
        keyExtractor={(item) => item.id}
        ListEmptyComponent={
          <Text style={styles.emptyText}>No transactions yet</Text>
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 20,
    backgroundColor: '#fff',
    marginTop: 20,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#1c1c1e',
  },
  transactionItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#f2f2f7',
  },
  transactionLeft: {
    flex: 1,
  },
  recipient: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1c1c1e',
  },
  description: {
    fontSize: 14,
    color: '#8e8e93',
    marginTop: 2,
  },
  transactionRight: {
    alignItems: 'flex-end',
  },
  amount: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1c1c1e',
  },
  statusBadge: {
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 12,
    marginTop: 4,
  },
  statusText: {
    fontSize: 12,
    fontWeight: '500',
  },
  emptyText: {
    textAlign: 'center',
    color: '#8e8e93',
    marginTop: 20,
  },
});