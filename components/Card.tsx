import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

interface CardProps {
  title: string;
  value: string;
  icon: keyof typeof Ionicons.glyphMap;
  variant?: 'default' | 'warning' | 'success';
}

export function Card({ title, value, icon, variant = 'default' }: CardProps) {
  const getVariantStyles = () => {
    switch (variant) {
      case 'warning':
        return {
          backgroundColor: '#fff3cd',
          iconColor: '#856404',
        };
      case 'success':
        return {
          backgroundColor: '#d4edda',
          iconColor: '#155724',
        };
      default:
        return {
          backgroundColor: '#fff',
          iconColor: '#007AFF',
        };
    }
  };

  const variantStyles = getVariantStyles();

  return (
    <View style={[styles.card, { backgroundColor: variantStyles.backgroundColor }]}>
      <Ionicons name={icon} size={24} color={variantStyles.iconColor} />
      <Text style={styles.title}>{title}</Text>
      <Text style={styles.value}>{value}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    flex: 1,
    padding: 16,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 3.84,
    elevation: 5,
  },
  title: {
    fontSize: 14,
    color: '#8e8e93',
    marginTop: 12,
  },
  value: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#1c1c1e',
    marginTop: 4,
  },
});