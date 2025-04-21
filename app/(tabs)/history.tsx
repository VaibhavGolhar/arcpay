import React from 'react';
import { View, StyleSheet, FlatList } from 'react-native';
import { usePaymentStore } from '../../store/paymentStore';
import { Payment } from '../../types/payment';
import { TransactionItem } from '../../components/TransactionItem';

export default function HistoryScreen() {
  const payments = usePaymentStore((state) => 
    [...state.payments].sort((a, b) => b.timestamp - a.timestamp)
  );

  const renderItem = ({ item }: { item: Payment }) => (
    <TransactionItem payment={item} />
  );

  return (
    <View style={styles.container}>
      <FlatList
        data={payments}
        renderItem={renderItem}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContent}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  listContent: {
    padding: 20,
  },
});