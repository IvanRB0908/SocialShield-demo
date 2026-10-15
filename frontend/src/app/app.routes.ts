import { Routes } from '@angular/router';
import { Home } from './home/home';
import { Login } from './login/login';
import { Register } from './register/register';
import { Profile } from './profile/profile';

export const routes: Routes = [
	{ path: '', component: Home },
	{ path: 'login', component: Login },
	{ path: 'registro', component: Register },
	{ path: 'perfil', component: Profile },
	{ path: '**', redirectTo: '' },
];
