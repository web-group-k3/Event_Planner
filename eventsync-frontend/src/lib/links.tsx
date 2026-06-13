import {
  Globe,
  ExternalLink,
  BookOpen,
  Link,
  Video,
  Code,
  Palette,
  AtSign,
} from "lucide-react";
import { JSX } from "react";

export interface ParsedLink {
  platform: string;
  url: string;
  icon: JSX.Element;
  label: string;
  color: string;
}

// Map platform keywords → { icon, label, accent color }
const PLATFORM_MAP: Record<string, { icon: JSX.Element; label: string; color: string }> = {
  linkedin:  { icon: <Link      className="w-3.5 h-3.5" />, label: "LinkedIn",  color: "#38bdf8" },
  twitter:   { icon: <AtSign    className="w-3.5 h-3.5" />, label: "Twitter",   color: "#38bdf8" },
  github:    { icon: <Code      className="w-3.5 h-3.5" />, label: "GitHub",    color: "#a3ff12" },
  youtube:   { icon: <Video     className="w-3.5 h-3.5" />, label: "YouTube",   color: "#ff4d6d" },
  dribbble:  { icon: <Palette   className="w-3.5 h-3.5" />, label: "Dribbble",  color: "#ff4d6d" },
  scholar:   { icon: <BookOpen  className="w-3.5 h-3.5" />, label: "Scholar",   color: "#a3ff12" },
  website:   { icon: <Globe     className="w-3.5 h-3.5" />, label: "Website",   color: "#94a3b8" },
};

/**
 * Parse speaker.links:
 *   • JSON object  → {"linkedin":"https://...","github":"https://..."}
 *   • Plain URL    → "https://linkedin.com/in/..."
 */
export function parseLinks(raw?: string | null): ParsedLink[] {
  if (!raw) return [];

  // JSON object
  try {
    const obj = JSON.parse(raw);
    if (typeof obj === "object" && obj !== null) {
      return Object.entries(obj).map(([key, val]) => {
        const platform = key.toLowerCase();
        const meta = PLATFORM_MAP[platform] ?? {
          icon:  <ExternalLink className="w-3.5 h-3.5" />,
          label: key,
          color: "#94a3b8",
        };
        return { platform, url: String(val), ...meta };
      });
    }
  } catch {
    // not JSON
  }

  // Plain URL — detect platform from domain
  const lower = raw.toLowerCase();
  for (const [key, meta] of Object.entries(PLATFORM_MAP)) {
    if (lower.includes(key)) {
      return [{
        platform: key,
        url: raw.startsWith("http") ? raw : `https://${raw}`,
        ...meta,
      }];
    }
  }

  // Unknown URL
  return [{
    platform: "link",
    url: raw.startsWith("http") ? raw : `https://${raw}`,
    icon:  <ExternalLink className="w-3.5 h-3.5" />,
    label: "Link",
    color: "#94a3b8",
  }];
}