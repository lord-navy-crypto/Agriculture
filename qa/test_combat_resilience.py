"""Verify the real entity classes apply the nonlethal player-weapon policy."""
import pathlib, unittest
R=pathlib.Path(__file__).resolve().parents[1]
class CombatResilienceSourceTest(unittest.TestCase):
    def test_each_entity_uses_lethal_hit_escape(self):
        for name,marker in (("GugugagaEntity.java","GUGU_WEAPON_SURVIVED"),
                            ("RedactorEntity.java","LAPIS_WEAPON_SURVIVED"),
                            ("YellowSmilerEntity.java","YELLOW_WEAPON_SURVIVED")):
            s=(R/"src/main/java/io/github/lordnavycrypto/gugugaga/entity"/name).read_text()
            self.assertIn("HorrorCombatPolicy.shouldEscapeLethalHit",s)
            self.assertIn("HorrorCombatPolicy.shouldBlockPlayerHit",s)
            self.assertIn(marker,s)
    def test_play_mode_enables_real_threats_for_joined_players(self):
        g=(R/"src/main/java/io/github/lordnavycrypto/gugugaga/entity/GugugagaEntity.java").read_text()
        r=(R/"src/main/java/io/github/lordnavycrypto/gugugaga/entity/RedactorEntity.java").read_text()
        self.assertIn("HauntGameplayRuntime.isActiveParticipant(player)",g)
        self.assertIn("HauntGameplayRuntime.isActiveParticipant(player)",r)
        self.assertIn("player.hurt(world.damageSources().mobAttack(this),3.0f)",g)
    def test_e2e_contains_real_player_attack_damage_checks(self):
        s=(R/"qa/gameplay_e2e.py").read_text()
        for token in ("GUGU_WEAPON_SURVIVED","LAPIS_WEAPON_SURVIVED","YELLOW_WEAPON_SURVIVED"):
            self.assertIn(token,s)
        self.assertIn("minecraft:player_attack by @s",s)
if __name__=="__main__": unittest.main()
