import dayjs from 'dayjs';
import { IIssue } from 'app/shared/model/issue.model';

export interface IComment {
  id?: number;
  createdDate?: string | null;
  subject?: string | null;
  comment?: string | null;
  issue?: IIssue | null;
}

export const defaultValue: Readonly<IComment> = {};
