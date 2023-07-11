export interface ICategory {
  id?: number;
  code?: string | null;
  description?: string | null;
  subcategories?: ICategory[] | null;
  parent?: ICategory | null;
  category?: ICategory | null;
}

export const defaultValue: Readonly<ICategory> = {};
