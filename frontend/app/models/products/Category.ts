// 🏷️ CATEGORÍA (Estructura de datos)
// Define cómo es una categoría en TypeScript:
// - id: identificador único
// - name: nombre de la categoría
// Usado para validar datos del backend

export interface Category {
  id: string;
  name: string;
}

export interface CreateCategoryRequest {
  name: string;
}

export interface UpdateCategoryRequest {
  name: string;
}
