import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Payment } from '../types/payment';
import { Ionicons } from '@expo/vector-icons';

interface TransactionItemProps {
  payment: Payment;
}

export function TransactionItem({ payment }: TransactionItemProps) {
  const getStatusColor = (status: Payment['status']) => {
    switch (status) {
      case 'completed':
        return '#28a745';
      case 'pending':
        return '#ffc107';
      case 'failed':
        return '#dc3545';
      default:
        return '#6c757d';
    }
  };

  const getStatusIcon = (status: Payment['status']) => {
    switch (status) {
      case 'completed':
        return 'checkmark-circle';
      case 'pending':
        return 'time';
      case 'failed':
        return 'close-circle';
      default:
        return 'help-circle';
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.recipient}>{payment.recipient}</Text>
        <Text style={styles.amount}>
          ${payment.amount.toFixed(2)}
        </Text>
      </View>

      <Text style={styles.description}>{payment.description}</Text>

      <View style={styles.footer}>
        <Text style={styles.date}>
          {new Date(payment.timestamp).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
          })}
        </Text>

        <View style={styles.statusContainer}>
          <Ionicons
            name={getStatusIcon(payment.status)}
            size={16}
            color={getStatusColor(payment.status)}
            style={styles.statusIcon}
          />
          <Text style={[
            styles.status,
            { color: getStatusColor(payment.status) }
          ]}>
            {payment.status.charAt(0).toUpperCase() + payment.status.slice(1)}
          </Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 12,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  recipient: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#1c1c1e',
  },
  amount: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#007AFF',
  },
  description: {
    fontSize: 14,
    color: '#8e8e93',
    marginBottom: 12,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  date: {
    fontSize: 12,
    color: '#8e8e93',
  },
  statusContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statusIcon: {
    marginRight: 4,
  },
  status: {
    fontSize: 12,
    fontWeight: '500',
  },
});