export interface ICustomer {
  id?: number;
  firstName?: string | null;
  secondName?: string | null;
  email?: string | null;
  phoneNumber?: string | null;
}

export const defaultValue: Readonly<ICustomer> = {};
