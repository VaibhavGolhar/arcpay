import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { usePaymentStore } from '../../store/paymentStore';
import { Card } from '../../components/Card';
import { RecentTransactions } from '../../components/RecentTransactions';

export default function HomeScreen() {
  const payments = usePaymentStore((state) => state.payments);
  
  const totalAmount = payments.reduce((sum, payment) => {
    if (payment.status === 'completed') {
      return sum + payment.amount;
    }
    return sum;
  }, 0);

  const pendingPayments = payments.filter(
    (payment) => payment.status === 'pending'
  ).length;

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.welcomeText}>Welcome back!</Text>
        <Text style={styles.dateText}>
          {new Date().toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
          })}
        </Text>
      </View>

      <View style={styles.cardsContainer}>
        <Card
          title="Total Transactions"
          value={`₹${totalAmount.toFixed(2)}`}
          icon="wallet-outline"
        />
        <Card
          title="Pending Payments"
          value={pendingPayments.toString()}
          icon="time-outline"
          variant="warning"
        />
      </View>

      <RecentTransactions />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    padding: 20,
    backgroundColor: '#fff',
  },
  welcomeText: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#1c1c1e',
  },
  dateText: {
    fontSize: 14,
    color: '#8e8e93',
    marginTop: 4,
  },
  cardsContainer: {
    flexDirection: 'row',
    padding: 20,
    gap: 16,
  },
});