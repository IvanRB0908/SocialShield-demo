import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

interface AppInfo {
  title: string;
}

@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  readonly appInfo: AppInfo = {
    title: 'SocialShield',
  };
}
