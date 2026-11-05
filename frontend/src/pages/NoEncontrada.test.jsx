import { screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { render } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import NoEncontrada from './NoEncontrada.jsx';

describe('NoEncontrada', () => {
  it('muestra el mensaje de página inexistente', () => {
    render(
      <MemoryRouter>
        <NoEncontrada />
      </MemoryRouter>,
    );

    expect(screen.getByRole('heading', { name: 'Página no encontrada' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Volver al panel' })).toHaveAttribute('href', '/');
  });
});
