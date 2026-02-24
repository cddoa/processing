import processing.core.PApplet;
import java.util.ArrayList;

/**
 * ParticleSprayingFountain
 * ------------------------
 * A Processing feature that spawns colorful particles at the mouse position.
 * Particles are affected by gravity and wind, bounce off walls/floor,
 * and fade out over time.
 *
 * Controls:
 * - Drag mouse: spray particles
 * - Press 'C': clear all particles
 * - Press 'M': return to menu (handled elsewhere)
 */
public class ParticleSprayingFountain implements Feature {

    // Reference to the Processing sketch (used for drawing and input)
    private PApplet p;

    // List of active particles currently on screen
    private ArrayList<Particle> particles = new ArrayList<>();

    // Environmental forces applied to particles each frame
    private float gravity = 0.15f;
    private float wind = 0.05f;

    /**
     * Constructor
     * @param parent The Processing PApplet running this feature
     */
    public ParticleSprayingFountain(PApplet parent) {
        this.p = parent;
    }

    /**
     * Updates all particles each frame.
     * Dead particles are removed to prevent memory buildup.
     */
    @Override
    public void update() {
        // Iterate backwards so removal does not break indexing
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle part = particles.get(i);
            part.update();

            // Remove particles once their lifespan reaches zero
            if (part.isDead()) {
                particles.remove(i);
            }
        }
    }

    /**
     * Draws all particles to the screen.
     * Background clearing is handled by Basic.java.
     */
    @Override
    public void display() {
        for (Particle part : particles) {
            part.display();
        }
    }

    /**
     * Handles mouse input.
     * While the mouse is pressed, spawn multiple particles per frame
     * to create a spraying effect.
     */
    @Override
    public void handleMouse() {
        if (p.mousePressed) {
            // Spawn multiple particles per frame for a denser spray
            for (int i = 0; i < 5; i++) {
                particles.add(new Particle(p.mouseX, p.mouseY));
            }
        }
    }

    /**
     * Handles keyboard input.
     * Pressing 'C' clears all particles from the screen.
     */
    @Override
    public void handleKeys() {
        if (p.keyPressed && (p.key == 'c' || p.key == 'C')) {
            particles.clear();
        }
    }

    /**
     * Instructions displayed in the menu system.
     */
    @Override
    public String getInstructions() {
        return "DRAG MOUSE to spray | Press 'C' to clear | Press 'M' for Menu";
    }

    /**
     * Particle
     * --------
     * Represents a single particle with position, velocity, color,
     * and a lifespan that fades out over time.
     */
    class Particle {

        // Position
        private float x, y;

        // Velocity
        private float vx, vy;

        // How long the particle remains alive (also used as alpha)
        private float lifespan;

        // Color hue (used with HSB color mode)
        private float hue;

        /**
         * Creates a particle at the given position with random velocity and color.
         * @param sx Starting x-position
         * @param sy Starting y-position
         */
        Particle(float sx, float sy) {
            x = sx;
            y = sy;

            // Random direction and speed for spray effect
            float angle = p.random(p.TWO_PI);
            float speed = p.random(1, 5);
            vx = p.cos(angle) * speed;
            vy = p.sin(angle) * speed;

            // Full lifespan (also used as alpha for fading)
            lifespan = 255;

            // Random color hue
            hue = p.random(360);
        }

        /**
         * Updates particle physics and reduces lifespan.
         */
        void update() {
            // Apply forces
            vy += gravity;
            vx += wind;

            // Update position
            x += vx;
            y += vy;

            // Bounce off the floor with some energy loss
            if (y > p.height - 10) {
                vy *= -0.7f;
                y = p.height - 10;
            }

            // Bounce off the walls with slight damping
            if (x > p.width || x < 0) {
                vx *= -0.9f;
            }

            // Fade out over time
            lifespan -= 2.0f;
        }

        /**
         * Renders the particle on screen.
         * Uses HSB color mode with alpha for fading.
         */
        void display() {
            p.noStroke();
            p.fill(hue, 80, 100, lifespan);
            p.circle(x, y, 8);
        }

        /**
         * @return true if the particle should be removed
         */
        boolean isDead() {
            return lifespan <= 0;
        }
    }
}