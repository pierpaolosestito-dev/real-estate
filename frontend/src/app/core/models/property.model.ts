import { Location } from './location.model';

export type PropertyType =
  | 'APPARTAMENTO'
  | 'VILLA'
  | 'MONOLOCALE'
  | 'UFFICIO';

export interface Property {
  id: number;
  tipo: PropertyType;
  superficieMq: number;
  stanze: number;
  bagni: number;
  location: Location;
}

export interface CreatePropertyPayload {
  tipo: PropertyType;
  superficieMq: number;
  stanze: number;
  bagni: number;
  location: Location;
}