import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { CabeceraTabla, Campo, Estado, MensajeError } from './ui.jsx';

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

  it('Campo asocia la etiqueta al control y anuncia el error como descripción', () => {
    render(
      <Campo etiqueta="Nombre" error="Obligatorio">
        {(atributos) => <input {...atributos} />}
      </Campo>,
    );
    // El error no debe formar parte del nombre accesible del campo.
    const campo = screen.getByRole('textbox', { name: 'Nombre' });
    expect(campo).toHaveAccessibleDescription('Obligatorio');
    expect(campo).toBeInvalid();
  });

  it('Campo sin error no marca el control como inválido', () => {
    render(
      <Campo etiqueta="Dirección IP">
        {(atributos) => <input {...atributos} placeholder="192.168.1.10" />}
      </Campo>,
    );
    const campo = screen.getByRole('textbox', { name: 'Dirección IP' });
    expect(campo).not.toHaveAttribute('aria-describedby');
    expect(campo).toBeValid();
  });

  it('CabeceraTabla pinta un encabezado por columna, incluida la de acciones', () => {
    render(
      <table>
        <CabeceraTabla columnas={['Fecha', 'IP', '']} />
      </table>,
    );
    const encabezados = screen.getAllByRole('columnheader');
    expect(encabezados.map((th) => th.textContent)).toEqual(['Fecha', 'IP', '']);
  });
});
