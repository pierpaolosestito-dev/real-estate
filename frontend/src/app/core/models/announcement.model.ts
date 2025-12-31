import { Property } from './property.model';
import { User } from './user.model';

export type AnnouncementType = 'VENDITA' | 'AFFITTO';

export interface Announcement {
  id: number;
  titolo: string;
  descrizione: string;
  prezzo: number;
  tipo: AnnouncementType;
  imageUrl:string;
  immobile: Property;
  venditore: User;
  dataPubblicazione: Date;
}
