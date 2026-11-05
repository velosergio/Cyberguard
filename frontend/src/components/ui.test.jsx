import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { Campo, Estado, MensajeError } from './ui.jsx';

describe('componentes ui', () => {
  it('MensajeError no renderiza sin texto', () => {
    const { container } = render(<MensajeError mensaje={null} />);
    expect(container).toBeEmptyDOMElement();
  });

  it('MensajeError muestra el aviso', () => {
    render(<MensajeError mensaje="Falló la carga" />);
    expect(screen.getByText('Falló la carga')).toBeInTheDocument();
  });

  it('Estado muestra cargando o error', () => {
    const { rerender } = render(<Estado cargando error={null} />);
    expect(screen.getByText('Cargando…')).toBeInTheDocument();

    rerender(<Estado cargando={false} error="Sin datos" />);
    expect(screen.getByText('Sin datos')).toBeInTheDocument();
  });

  it('Campo muestra la etiqueta y el error del servidor', () => {
    render(
      <Campo etiqueta="Nombre" error="Obligatorio">
        <input />
      </Campo>,
    );
    expect(screen.getByText('Nombre')).toBeInTheDocument();
    expect(screen.getByText('Obligatorio')).toBeInTheDocument();
  });
});
