export interface IAvailability {
  id?: number;
  date?: string | null;
}

export class Availability implements IAvailability {
  constructor(
    public id?: number,
    public date?: string | null,
  ) {}
}
