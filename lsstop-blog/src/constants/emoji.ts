import emojiManifestJson from './emoji-manifest.json';

export const EMOJI_BASE_URL = (
  import.meta.env.VITE_EMOJI_BASE_URL || 'https://1bx248gw72461.vicp.fun/emoji'
).replace(/\/+$/, '');

export type EmojiAnimationType = 'apng' | 'lottie' | 'static';
export type EmojiCategory = 'super' | 'face';
export type QqEmojiType = 0 | 1 | 2 | 3;

export interface EmojiAsset {
  token: string;
  label: string;
  id: string;
  category: EmojiCategory;
  qqType: QqEmojiType;
  animationType: EmojiAnimationType;
  staticUrl: string;
  animatedUrl: string;
  standaloneWidth: number;
  standaloneHeight: number;
}

export interface EmojiGroup {
  id: EmojiCategory;
  label: string;
  emojis: EmojiAsset[];
}

interface EmojiManifestAsset {
  token: string;
  label: string;
  id: string;
  category: EmojiCategory;
  qqType: QqEmojiType;
  animationType: EmojiAnimationType;
  staticFile: string;
  animatedFile: string;
  standaloneWidth: number;
  standaloneHeight: number;
}

interface EmojiManifest {
  version: number;
  groups: Array<{
    id: EmojiCategory;
    label: string;
    emojis: EmojiManifestAsset[];
  }>;
}

const emojiManifest = emojiManifestJson as EmojiManifest;

function resolveEmojiUrl(fileName: string): string {
  return `${EMOJI_BASE_URL}/${fileName}`;
}

function createEmojiAsset(asset: EmojiManifestAsset): EmojiAsset {
  return {
    token: asset.token,
    label: asset.label,
    id: asset.id,
    category: asset.category,
    qqType: asset.qqType,
    animationType: asset.animationType,
    staticUrl: resolveEmojiUrl(asset.staticFile),
    animatedUrl: resolveEmojiUrl(asset.animatedFile),
    standaloneWidth: asset.standaloneWidth,
    standaloneHeight: asset.standaloneHeight,
  };
}

export const EmojiGroups: EmojiGroup[] = emojiManifest.groups.map((group) => ({
  id: group.id,
  label: group.label,
  emojis: group.emojis.map(createEmojiAsset),
}));

const EmojiList = Object.fromEntries(
  EmojiGroups.flatMap((group) => group.emojis.map((emoji) => [emoji.token, emoji])),
) as Record<string, EmojiAsset>;

export default EmojiList;
