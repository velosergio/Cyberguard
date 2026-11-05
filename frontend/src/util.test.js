import { describe, expect, it } from 'vitest';
import { ahoraLocal, formatFecha } from './util.js';

describe('util', () => {
  it('formatFecha formatea ISO a dd/MM/yyyy HH:mm', () => {
    expect(formatFecha('2026-11-05T14:05:00')).toMatch(/05\/11\/2026 14:05/);
  });

  it('formatFecha usa guión tipográfico si falta el valor', () => {
    expect(formatFecha(null)).toBe('—');
    expect(formatFecha(undefined)).toBe('—');
  });

  it('ahoraLocal produce valor válido para datetime-local', () => {
    expect(ahoraLocal()).toMatch(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/);
  });
});
