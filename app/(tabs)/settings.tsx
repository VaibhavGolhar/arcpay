import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView } from 'react-native';
import { Ionicons } from '@expo/vector-icons';

export default function SettingsScreen() {
  const settingsGroups = [
    {
      title: 'Account',
      items: [
        { icon: 'person-outline', label: 'Profile' },
        { icon: 'notifications-outline', label: 'Notifications' },
        { icon: 'shield-outline', label: 'Security' },
      ],
    },
    {
      title: 'Payments',
      items: [
        { icon: 'card-outline', label: 'Payment Methods' },
        { icon: 'repeat-outline', label: 'Recurring Payments' },
        { icon: 'wallet-outline', label: 'Transaction Limits' },
      ],
    },
    {
      title: 'Support',
      items: [
        { icon: 'help-circle-outline', label: 'Help Center' },
        { icon: 'chatbox-outline', label: 'Contact Support' },
        { icon: 'document-text-outline', label: 'Terms of Service' },
      ],
    },
  ];

  return (
    <ScrollView style={styles.container}>
      {settingsGroups.map((group, groupIndex) => (
        <View key={group.title} style={styles.group}>
          <Text style={styles.groupTitle}>{group.title}</Text>
          <View style={styles.groupContent}>
            {group.items.map((item, itemIndex) => (
              <TouchableOpacity
                key={item.label}
                style={[
                  styles.item,
                  itemIndex === group.items.length - 1 && styles.lastItem,
                ]}
              >
                <View style={styles.itemContent}>
                  <Ionicons name={item.icon as any} size={24} color="#007AFF" />
                  <Text style={styles.itemLabel}>{item.label}</Text>
                </View>
                <Ionicons name="chevron-forward" size={24} color="#c7c7cc" />
              </TouchableOpacity>
            ))}
          </View>
        </View>
      ))}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  group: {
    marginTop: 20,
  },
  groupTitle: {
    fontSize: 13,
    fontWeight: '600',
    color: '#8e8e93',
    marginLeft: 20,
    marginBottom: 8,
    textTransform: 'uppercase',
  },
  groupContent: {
    backgroundColor: '#fff',
  },
  item: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#f2f2f7',
  },
  lastItem: {
    borderBottomWidth: 0,
  },
  itemContent: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  itemLabel: {
    fontSize: 16,
    marginLeft: 12,
    color: '#1c1c1e',
  },
});